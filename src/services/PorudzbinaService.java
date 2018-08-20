package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Artikal;
import beans.Dostavljac;
import beans.Kupac;
import beans.Porudzbina;
import beans.PorudzbinaToModify;
import beans.PorudzbinaToProcess;
import beans.StavkaForPorudzbina;
import beans.StavkaPorudzbine;
import beans.StavkaToPut;
import beans.User;
import dao.ArtikalDAO;
import dao.PorudzbinaDAO;
import dao.RestoranDAO;
import dao.UserDAO;
import dao.VoziloDAO;

@Path("/porudzbine")
public class PorudzbinaService {
	

	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	

	@GET
	@Path("/getPorudzbineList")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Porudzbina> getJustPorudzbinaList() {
		
		PorudzbinaDAO porudzbine = getPorudzbinaDAO();
		
		
		return porudzbine.getPorudzbinalist();
	}
	
	@GET
	@Path("/getPorudzbineMap")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<Integer, Porudzbina> getJustPorudzbinaMap() {
		
		PorudzbinaDAO porudzbine = getPorudzbinaDAO();
		
		
		return filterMap(porudzbine.getPorudzbinemap());
	}
	
	private HashMap<Integer,Porudzbina> filterMap(HashMap<Integer, Porudzbina> map) {
		HashMap<Integer,Porudzbina> filtered = new HashMap<>();
		
		for (Porudzbina p: getPorudzbinaDAO().getPorudzbinemap().values()) {
			if (p.isAktivna()) {
				filtered.put(p.getIdPorudzbine(), p);
			}
		}
		
		
		return filtered;
	}
	
	
	@PUT
	@Path("/putTrenutneStavke")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response putTrenutneStavke(StavkaToPut stavkaToPut) {
		
		Artikal a = getArtikalDAO().getArtikalmap().get(stavkaToPut.artikalId);
		if (a == null ) {
			return Response.status(400).entity("Ne postoji artikal sa datim id-em.").build();
		}
		
		StavkaPorudzbine stavka = new StavkaPorudzbine(a, stavkaToPut.porcija);
		
		
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		if (!getUserDAO().getKupacmap().containsKey(stavkaToPut.username)) {
			return Response.status(400).entity("Greska, kupac pogresan.").build();
		}
		
		if (!porudzbinaDAO.getTrenutneStavke().containsKey(stavkaToPut.username)) {
			HashMap <Integer, StavkaPorudzbine> stavke = new HashMap<>();
			porudzbinaDAO.getTrenutneStavke().put(stavkaToPut.username, stavke);
		}
		
		HashMap<Integer, StavkaPorudzbine> temp = porudzbinaDAO.getTrenutneStavke().get(stavkaToPut.username);
		
		if (!temp.containsKey(stavkaToPut.artikalId)) {
			temp.put(stavkaToPut.artikalId, stavka);
		} else {
			temp.get(stavkaToPut.artikalId).refreshPorcije(stavka.getBrojPorcija());
		}
		
		
		
		return Response.status(200).build();
		
	}
	
	@POST
	@Path("/removeFromTrenutneStavke")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeFromTrenutneStavke(StavkaToPut stavkaToRemove) {
		
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		
		if(!porudzbinaDAO.getTrenutneStavke().containsKey(stavkaToRemove.username)) {
			return Response.status(400).entity("Trenutne stavke usera not found.").build();
		}
		
		HashMap<Integer, StavkaPorudzbine> temp = porudzbinaDAO.getTrenutneStavke().get(stavkaToRemove.username);
		
		if (!temp.containsKey(stavkaToRemove.artikalId)) {
			return Response.status(400).entity("Not Found artikalID.").build();
		}
		
		temp.remove(stavkaToRemove.artikalId);
		
		return Response.status(200).build();
	}
	
	@GET
	@Path("/clearTrenutneStavke")
	@Produces(MediaType.APPLICATION_JSON)
	public Response clearTrenutneStavke() {
		
		getPorudzbinaDAO().getTrenutneStavke().clear();
		return Response.status(200).build();
	}
	
	@GET
	@Path("/getTrenutneStavkeById/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<StavkaPorudzbine> getTrenutneStavkeById(@PathParam("username") String username) {
		
		if (!getUserDAO().getKupacmap().containsKey(username)) {
			return null;
		}
		
		if (getPorudzbinaDAO().getTrenutneStavke().get(username) != null) {
			return getPorudzbinaDAO().getTrenutneStavke().get(username).values();
		} else {
			return null;
		}
		

	}
	
	@POST
	@Path("/makeOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response makeOrder(PorudzbinaToProcess ptp) {
		
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		UserDAO userDAO = getUserDAO();
		
		ArrayList<StavkaPorudzbine> stavke = new ArrayList<>();
		
		for(StavkaForPorudzbina sfp: ptp.stavke) {
			Artikal a = getArtikalDAO().getArtikalmap().get(sfp.artikalId);
			if (a == null) {
				return Response.status(400).entity("Artikal ID wrong").build();
			}
			
			a.setPopularnost(a.getPopularnost()+1);
			getArtikalDAO().writeArtikli(ctx.getRealPath(""));
			
			StavkaPorudzbine stavka = new StavkaPorudzbine(a, sfp.brojPorcija);
			stavke.add(stavka);
		}
		
		int idToAdd;
		Set keys = porudzbinaDAO.getPorudzbinemap().keySet();
		if (keys.isEmpty()) {
			idToAdd = 1;
		} else {
			idToAdd = 1 + (int) Collections.max(keys, null);
		}
		
		
		Porudzbina p = new Porudzbina(idToAdd, stavke, ptp.ukupnaCena, ptp.datum, ptp.kupacUsername, null, 1, ptp.napomena);
		
		if (porudzbinaDAO.getPorudzbinemap().containsKey(idToAdd)) {
			System.out.println("Porudzbina ID not valid");
			return Response.status(500).build();
		}
		
		if (!userDAO.getUsermap().containsKey(ptp.kupacUsername)) {
			return Response.status(500).entity("Username in usermap not found.").build();
		}
		
		
		porudzbinaDAO.getPorudzbinemap().put(idToAdd, p);
		porudzbinaDAO.writePorudzbina(ctx.getRealPath(""), p);
		Kupac k = (Kupac) userDAO.getUsermap().get(ptp.kupacUsername);
		k.getPorudzbine().add(p);
		userDAO.writeUsers(ctx.getRealPath(""));
		
		return Response.status(200).build();
	}
	
	@DELETE
	@Path("/deletePorudzbina/{idPorudzbine}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePorudzbina(@PathParam("idPorudzbine") int idPorudzbine) {
		
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		UserDAO userDAO = getUserDAO();
		
		if(!porudzbinaDAO.getPorudzbinemap().containsKey(idPorudzbine)) {
			return Response.status(400).entity("Id porudzbine not found in porudzbine map.").build();
		}
		
		porudzbinaDAO.getPorudzbinemap().get(idPorudzbine).setAktivna(false);
		porudzbinaDAO.writePorudzbine(ctx.getRealPath(""));
		
		String username = porudzbinaDAO.getPorudzbinemap().get(idPorudzbine).getKupacUsername();
		
		if(!userDAO.getUsermap().containsKey(username)) {
			return Response.status(400).entity("User not found").build();
		}
		
		Kupac k = (Kupac) userDAO.getUsermap().get(username);

		for (int i = 0; i < k.getPorudzbine().size(); i++) {
			if (k.getPorudzbine().get(i).getIdPorudzbine() == idPorudzbine)
				k.getPorudzbine().remove(i);
		}
		
		userDAO.writeUsers(ctx.getRealPath(""));
		
		
		return Response.status(200).build();
	}
	
	@POST
	@Path("/modifyPorudzbina")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifyPorudzbina(PorudzbinaToModify ptm) {
		
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		UserDAO userDAO = getUserDAO();
		ArrayList<StavkaPorudzbine> stavke = new ArrayList<>();
		
		for(StavkaForPorudzbina sfp: ptm.stavke) {
			Artikal a = getArtikalDAO().getArtikalmap().get(sfp.artikalId);
			if (a == null) {
				return Response.status(400).entity("Artikal ID wrong").build();
			}
			
			StavkaPorudzbine stavka = new StavkaPorudzbine(a, sfp.brojPorcija);
			stavke.add(stavka);
		}
		
		Porudzbina p = new Porudzbina(ptm.idPorudzbine, stavke, ptm.ukupnaCena, ptm.datum, ptm.kupacUsername, ptm.dostavljacUsername, ptm.statusPorudzbine, ptm.napomena);
		
		if (!porudzbinaDAO.getPorudzbinemap().containsKey(ptm.idPorudzbine)) {
			System.out.println("Porudzbina ID not valid");
			return Response.status(500).build();
		}
		
		if (!userDAO.getUsermap().containsKey(ptm.kupacUsername)) {
			return Response.status(500).entity("Username in usermap not found.").build();
		}
		
		
		
		porudzbinaDAO.getPorudzbinemap().replace(ptm.idPorudzbine, p);
		porudzbinaDAO.writePorudzbine(ctx.getRealPath(""));
		Kupac k = (Kupac) userDAO.getUsermap().get(ptm.kupacUsername);
		Dostavljac d = (Dostavljac) userDAO.getUsermap().get(ptm.dostavljacUsername);
		
		for (int i = 0; i < k.getPorudzbine().size(); i++) {
			if (k.getPorudzbine().get(i).getIdPorudzbine() == ptm.idPorudzbine) {
				k.getPorudzbine().remove(i);
				k.getPorudzbine().add(p);
			}
		}
		
		for (int i = 0; i < d.getPorudzbine().size(); i++) {
			if (d.getPorudzbine().get(i).getIdPorudzbine() == ptm.idPorudzbine) {
				d.getPorudzbine().remove(i);
				d.getPorudzbine().add(p);
			}
		}
		
		userDAO.writeUsers(ctx.getRealPath(""));
		
		return Response.status(200).build();
	}
	
	@PUT
	@Path("/dostavi/{idPorudzbine}/{usernameDost}/{usernameKup}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response dostavi(@PathParam("idPorudzbine") int idPorudzbine, @PathParam("usernameDost") String dostavljac, @PathParam("usernameKup") String kupac) {
		
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		UserDAO userDAO = getUserDAO();
		
		
		if(!porudzbinaDAO.getPorudzbinemap().containsKey(idPorudzbine)) {
			return Response.status(400).entity("idPorudzbine not valid.").build();
		}
		
		
		porudzbinaDAO.getPorudzbinemap().get(idPorudzbine).setStatusPorudzbine(4);
		porudzbinaDAO.writePorudzbine(ctx.getRealPath(""));
		
		
		Kupac k = (Kupac) userDAO.getUsermap().get(kupac);
		Dostavljac d = (Dostavljac) userDAO.getUsermap().get(dostavljac);
		
		for (int i = 0; i < k.getPorudzbine().size(); i++) {
			if (k.getPorudzbine().get(i).getIdPorudzbine() == idPorudzbine) {
				k.getPorudzbine().get(i).setStatusPorudzbine(4);
			}
		}
		
		for (int i = 0; i < d.getPorudzbine().size(); i++) {
			if (d.getPorudzbine().get(i).getIdPorudzbine() == idPorudzbine) {
				d.getPorudzbine().get(i).setStatusPorudzbine(4);
			}
		}
		
		
		userDAO.writeUsers(ctx.getRealPath(""));
		
		return Response.status(200).build();
		
	}
	
	
	private PorudzbinaDAO getPorudzbinaDAO() {
		
		PorudzbinaDAO porudzbine = (PorudzbinaDAO) ctx.getAttribute("porudzbine");
		
		if (porudzbine == null) {
			
			String contextPath = ctx.getRealPath("");
			System.out.println("Context Path: " + contextPath);
			porudzbine = new PorudzbinaDAO(contextPath, getArtikalDAO());
			//artikli.initializeRestorani(new RestoranDAO(contextPath));
			ctx.setAttribute("porudzbine", porudzbine);
		}
		
		System.out.println("getArtikalDAO(): " + porudzbine.toString());
		return porudzbine;
		
		
	}
	
	
	private ArtikalDAO getArtikalDAO() {
		
		ArtikalDAO artikli = (ArtikalDAO) ctx.getAttribute("artikli");
		
		if (artikli == null) {
			
			String contextPath = ctx.getRealPath("");
			System.out.println("Context Path: " + contextPath);
			artikli = new ArtikalDAO(contextPath);
			//artikli.initializeRestorani(new RestoranDAO(contextPath));
			ctx.setAttribute("artikli", artikli);
		}
		
		
		return artikli;
		
		
	}
	
	
	private RestoranDAO getRestoranDAO() {
		
		
		RestoranDAO restorani = (RestoranDAO) ctx.getAttribute("restorani");
	
		if (restorani == null) {
			String contextPath = ctx.getRealPath("");
			System.out.println("Context Path: " + contextPath);
			restorani = new RestoranDAO(contextPath, getArtikalDAO());
			ctx.setAttribute("restorani", restorani);
		}
		
		return restorani;
		
	}
	
	private UserDAO getUserDAO() {
		
		UserDAO users = (UserDAO) ctx.getAttribute("users");
		if (users == null) {
			
			String contextPath = ctx.getRealPath("");
			System.out.println("Context Path: " + contextPath);
			users = new UserDAO(contextPath, getPorudzbinaDAO(), getRestoranDAO(), getVoziloDAO());
			ctx.setAttribute("users", users);
		}
		
		return users;
	}
	
	private VoziloDAO getVoziloDAO() {
		
		VoziloDAO vozila = (VoziloDAO) ctx.getAttribute("vozila");
		
		if (vozila == null) {
			
			String contextPath = ctx.getRealPath("");
			System.out.println("Context Path: " + contextPath);
			vozila = new VoziloDAO(contextPath);
			//artikli.initializeRestorani(new RestoranDAO(contextPath));
			ctx.setAttribute("vozila", vozila);
		}
		
		return vozila;
		
		
	}

}
