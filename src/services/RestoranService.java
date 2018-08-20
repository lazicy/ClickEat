package services;

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

import beans.Kupac;
import beans.Restoran;
import beans.RestoranToAdd;
import beans.RestoranToModify;
import dao.ArtikalDAO;
import dao.PorudzbinaDAO;
import dao.RestoranDAO;
import dao.UserDAO;
import dao.VoziloDAO;
import jersey.repackaged.com.google.common.collect.Sets;

@Path("/restorani")
public class RestoranService {
	

	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	
	@GET
	@Path("/getRestoranList")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<Integer, Restoran> getJustRestoranList() {
		
		RestoranDAO restorani = getRestoranDAO();
		
		for (Restoran rTemp: restorani.getRestoranlist()) {
			System.out.println("GetRestoranList: " + rTemp.toString());
		}
		
		return filterMap(restorani.getRestoranmap());
	}
	
	@GET
	@Path("/getRestoranListList")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Restoran> getJustRestoranListList() {
		
		RestoranDAO restorani = getRestoranDAO();
		
		for (Restoran rTemp: restorani.getRestoranlist()) {
			System.out.println("GetRestoranList: " + rTemp.toString());
		}
		
		return filterMap(restorani.getRestoranmap()).values();
	}
	
	private HashMap<Integer,Restoran> filterMap(HashMap<Integer, Restoran> map) {
		HashMap<Integer,Restoran> filtered = new HashMap<>();
		
		for (Restoran r: getRestoranDAO().getRestoranmap().values()) {
			if (r.isAktivan()) {
				filtered.put(r.getId(), r);
			}
		}
		
		
		return filtered;
	}
	

	
	@GET
	@Path("/getRestoranById/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Restoran getRestoranById(@PathParam("id") int id) {
		
		return getRestoranDAO().getRestoranById(id);
	}
	
	@POST
	@Path("/noviRestoran")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response noviRestoran(RestoranToAdd restoranToAdd) {
		
		Set keys = getRestoranDAO().getRestoranmap().keySet();
		int idToAdd = 1 + (int) Collections.max(keys, null);
		
		Restoran r = new Restoran(restoranToAdd, idToAdd);
		RestoranDAO restoranDAO = getRestoranDAO();
		restoranDAO.getRestoranmap().put(idToAdd, r);
		restoranDAO.writeRestoran(ctx.getRealPath(""), r);
		
		return Response.status(200).build();
		
	}
	
	
	@POST
	@Path("/modifyRestoran")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifyRestoran(RestoranToModify restoranToModify) {
		
		Restoran r = new Restoran(restoranToModify);
		RestoranDAO restoranDAO = getRestoranDAO();
		UserDAO userDAO = getUserDAO();
		
		restoranDAO.getRestoranmap().replace(r.getId(), r);
		
		for (Kupac k : userDAO.getKupacmap().values()) {
			for (int i = 0; i<  k.getFavRestorani().size(); i++) {
				if (r.getId() == k.getFavRestorani().get(i).getId()) {
					k.getFavRestorani().remove(i);
					k.getFavRestorani().add(r);
				}
			}
		}
		
		try {
			restoranDAO.writeRestorani(ctx.getRealPath(""));
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(400).build();
		}
		
		return Response.status(200).build();
		
	}
	
	@DELETE
	@Path("/deleteRestoran/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRestoran(@PathParam("id") int restoranId) {
		
		System.out.println("here");
		RestoranDAO restoranDAO = getRestoranDAO();
		ArtikalDAO artikalDAO = getArtikalDAO();
		UserDAO userDAO = getUserDAO();

		if (!restoranDAO.getRestoranmap().containsKey(restoranId)) {
			return Response.status(400).build();
		}
		
		Restoran r = restoranDAO.getRestoranmap().get(restoranId);
		
		// restoran i artikli unutar restorana
		r.setAktivan(false);
		// celokupna lista artikala
		Set<Integer> keysJela = getRestoranDAO().getRestoranmap().get(restoranId).getJelaMap().keySet();
		Set<Integer> keysPica = getRestoranDAO().getRestoranmap().get(restoranId).getPicaMap().keySet();
		Set<Integer> keys = Sets.union(keysJela, keysPica);
		artikalDAO.deactivateArtikli(restoranId, keys);
		
		
		for (Kupac k : userDAO.getKupacmap().values()) {
			for (int i = 0; i<  k.getFavRestorani().size(); i++) {
				if (r.getId() == k.getFavRestorani().get(i).getId()) {
					k.getFavRestorani().remove(i);
				}
			}
		}
		
		
		try {
			restoranDAO.writeRestorani(ctx.getRealPath(""));
			artikalDAO.writeArtikli(ctx.getRealPath(""));
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(404).build();
		}
		
		return Response.status(200).build();
		
	}
	
	
	@PUT
	@Path("/addToFavRestorani/{restoranId}/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addToFavRestorani(@PathParam("restoranId") int restoranId, @PathParam("username") String username) {
		
		RestoranDAO restoranDAO = getRestoranDAO();
		UserDAO userDAO = getUserDAO();
		
		if(!restoranDAO.getRestoranmap().containsKey(restoranId)) {
			return Response.status(400).entity("Restoran ID not valid").build();
		}
		
		if (!userDAO.getUsermap().containsKey(username)) {
			return Response.status(400).entity("Username not valid").build();
		}
		
		Restoran r = restoranDAO.getRestoranmap().get(restoranId);
		Kupac k = (Kupac) userDAO.getUsermap().get(username);
		
		for (int i = 0; i < k.getFavRestorani().size(); i++) {
			if (k.getFavRestorani().get(i).getId() == r.getId()) {
				return Response.status(400).entity("Already in fav restorani list.").build();
			}
		}
		
		k.getFavRestorani().add(r);
		
		userDAO.writeUsers(ctx.getRealPath(""));
		
		return Response.status(200).build();
	}
	
	@DELETE
	@Path("/removeFromFavRestorani/{restoranId}/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeFromFavRestorani(@PathParam("restoranId") int restoranId, @PathParam("username") String username) {
		RestoranDAO restoranDAO = getRestoranDAO();
		UserDAO userDAO = getUserDAO();
		
		if(!restoranDAO.getRestoranmap().containsKey(restoranId)) {
			return Response.status(400).entity("Restoran ID not valid").build();
		}
		
		if (!userDAO.getUsermap().containsKey(username)) {
			return Response.status(400).entity("Username not valid").build();
		}
		
		Kupac k = (Kupac) userDAO.getUsermap().get(username);
		
		for (int i = 0; i < k.getFavRestorani().size(); i++) {
			if (k.getFavRestorani().get(i).getId() == restoranId) {
				k.getFavRestorani().remove(i);
			}
		}
		
		
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
