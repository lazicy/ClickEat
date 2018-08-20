package services;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Artikal;
import beans.ArtikalToAdd;
import beans.ArtikalToModify;
import beans.Porudzbina;
import beans.Restoran;
import dao.ArtikalDAO;
import dao.RestoranDAO;

@Path("/artikli")
public class ArtikalService {
	
	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	
	@GET
	@Path("/getArtikalList")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Artikal> getJustArtikalList() {
		
		ArtikalDAO  artikli = getArtikalDAO();
		
		for (Artikal aTemp: artikli.getArtikallist()) {
			System.out.println("GetArtikaList: " + aTemp.toString());
		}
		
		return filterMap(artikli.getArtikalmap()).values();
	}
	
	@GET
	@Path("/getArtikalMap")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<Integer, Artikal> getJustArtikalMap() {
		ArtikalDAO  artikli = getArtikalDAO();
		
		for (Artikal aTemp: artikli.getArtikallist()) {
			System.out.println("GetArtikaList: " + aTemp.toString());
		}
		
		return artikli.getArtikalmap();
	}
	
	private HashMap<Integer,Artikal> filterMap(HashMap<Integer, Artikal> map) {
		HashMap<Integer,Artikal> filtered = new HashMap<>();
		
		for (Artikal a: getArtikalDAO().getArtikalmap().values()) {
			if (a.isAktivan()) {
				filtered.put(a.getId(), a);
			}
		}
		return filtered;
	}
	
	@GET
	@Path("/getArtikalById/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Artikal getArtikalById(@PathParam("id") int id) {
		
		ArtikalDAO artikalDAO = getArtikalDAO();
		
		if (!artikalDAO.getArtikalmap().containsKey(id)) {
			return null;
		}

		return artikalDAO.getArtikalmap().get(id);
	}
	
	
	@POST
	@Path("/addArtikal")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addArtikal(ArtikalToAdd artikalToAdd) {
		
		ArtikalDAO artikalDAO = getArtikalDAO();
		RestoranDAO restoranDAO = getRestoranDAO();
		
		Set keys = getArtikalDAO().getArtikalmap().keySet();
		int idToAdd = 1 + (int) Collections.max(keys, null);
	
		
		if (!restoranDAO.getRestoranmap().containsKey(artikalToAdd.restoranId)) {
			return Response.status(400).build();
		}
		
		
		Artikal a = new Artikal(artikalToAdd, idToAdd);
		artikalDAO.getArtikalmap().put(a.getId(), a);
		if (artikalToAdd.tip == 1) {
			restoranDAO.getRestoranmap().get(artikalToAdd.restoranId).getJelaMap().put(idToAdd, a);
		} else if (artikalToAdd.tip == 2) {
			restoranDAO.getRestoranmap().get(artikalToAdd.restoranId).getPicaMap().put(idToAdd, a);
		}
		
		
		try {
			artikalDAO.writeArtikal(ctx.getRealPath(""), a);
			restoranDAO.writeRestorani(ctx.getRealPath(""));
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(404).build();
		}
		
		return Response.status(200).build();
	}
	
	@DELETE
	@Path("/deleteArtikal/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteArtikal(@PathParam("id") int id) {
		
		ArtikalDAO artikalDAO = getArtikalDAO();
		RestoranDAO restoranDAO = getRestoranDAO();
		
		if (!artikalDAO.getArtikalmap().containsKey(id)) {
			return Response.status(400).entity("Neodgovarajuc id artikla.").build();
		}
		
		Artikal a = artikalDAO.getArtikalmap().get(id);
		System.out.println("[ArtikalService]: Artikal " + a.getId() + ", tip " + a.getTip());
		
		// artikal lista
		a.setAktivan(false);
		
		// restoran lista
		if (!restoranDAO.getRestoranmap().containsKey(a.getRestoranId())) {
			System.out.println("Neodgovarajuc id restorana unutar artikla.");
			return Response.status(400).entity("Neodgovarajuc id restorana unutar artikla.").build();
		}
		Restoran r = restoranDAO.getRestoranmap().get(a.getRestoranId());
		restoranDAO.deactivateArtikal(r.getId(), a);
		
		try {
			artikalDAO.writeArtikli(ctx.getRealPath(""));
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(404).build();
		}
		
		return Response.status(200).build();
	}
	
	
	@POST
	@Path("/modifyArtikal")
	public Response modifyArtikal(ArtikalToModify artikalToModify) {
		
		Artikal a = new Artikal(artikalToModify);
		ArtikalDAO artikalDAO = getArtikalDAO();
		RestoranDAO restoranDAO = getRestoranDAO();
		
		if (!artikalDAO.getArtikalmap().containsKey(artikalToModify.id)) {
			System.out.println("Nije pronadjen artikal za modifikaciju.");
			return Response.status(400).entity("Nije pronadjen artikal za modifikaciju.").build();
		}
		
		if (!restoranDAO.getRestoranmap().containsKey(artikalToModify.restoranId)) {
			return Response.status(400).entity("Nije pronadjen restoran za zeljeni artikal").build();
		}
		
		artikalDAO.getArtikalmap().replace(artikalToModify.id, a);

		if (artikalToModify.tip == 1) {
			restoranDAO.getRestoranmap().get(artikalToModify.restoranId).getJelaMap().replace(artikalToModify.id, a);
		} else if (artikalToModify.tip == 2) {
			restoranDAO.getRestoranmap().get(artikalToModify.restoranId).getPicaMap().replace(artikalToModify.id, a);
		}
			

		try {
			artikalDAO.writeArtikli(ctx.getRealPath(""));
		} catch (Exception e) {
			System.out.println("Write artikli problem.");
			e.printStackTrace();
			return Response.status(400).build();
		}
		
		return Response.status(200).build();
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
		
		System.out.println("getArtikalDAO(): " + artikli.toString());
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
	

}
