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

import beans.Vozilo;
import beans.VoziloToAdd;
import beans.VoziloToModify;
import dao.UserDAO;
import dao.VoziloDAO;

@Path("/vozila")
public class VoziloService {
	
	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	
	@GET
	@Path("/getVozilaList")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Vozilo> getVozilaList() {
		
		VoziloDAO voziloDAO = getVoziloDAO();
		
		return voziloDAO.getVozilaList();
	};
	
	
	
	@GET
	@Path("/getVozilaMap")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<Integer, Vozilo> getVozilaMap() {
		
		VoziloDAO voziloDAO = getVoziloDAO();
		
		return filterMap(voziloDAO.getVozilaMap());
	};
	
	private HashMap<Integer, Vozilo> filterMap(HashMap<Integer, Vozilo> map) {
		HashMap<Integer,Vozilo> filtered = new HashMap<>();
		
		for (Vozilo v: getVoziloDAO().getVozilaMap().values()) {
			if (v.isAktivan()) {
				filtered.put(v.getId(), v);
			}
		}
		
		
		return filtered;
	}
	
	
	
	@GET
	@Path("/getVoziloById/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Vozilo getVoziloById(@PathParam("id") int id) {
		
		VoziloDAO voziloDAO = getVoziloDAO();
		
		return voziloDAO.getVozilaMap().get(id);
		
	}
	
	@POST
	@Path("/addVozilo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addVozilo(VoziloToAdd vta) {
		
		VoziloDAO voziloDAO = getVoziloDAO();
		
		Set keys = voziloDAO.getVozilaMap().keySet();
		int idToAdd = 1 + (int) Collections.max(keys, null);
		
		Vozilo v = new Vozilo(idToAdd, vta.marka, vta.model, vta.tip, vta.registracija, vta.godinaProizvodnje, false, vta.napomena, true);
		
		voziloDAO.getVozilaMap().put(idToAdd, v);
		voziloDAO.writeVozilo(ctx.getRealPath(""), v);
		
		return Response.status(200).build();
	}
	
	@DELETE
	@Path("/removeVozilo/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeVozilo(@PathParam("id") int id) {
		
		VoziloDAO voziloDAO = getVoziloDAO();
		
		if (!voziloDAO.getVozilaMap().containsKey(id)) {
			return Response.status(400).entity("Vozilo with given id not found.").build();
		}
		
		voziloDAO.getVozilaMap().get(id).setAktivan(false);
		voziloDAO.writeVozila(ctx.getRealPath(""));
		
		return Response.status(200).build();
	}
	
	@POST
	@Path("/modifyVozilo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifyVozilo(VoziloToModify vtm) {
		
		VoziloDAO voziloDAO = getVoziloDAO();
		
		if (!voziloDAO.getVozilaMap().containsKey(vtm.id)) {
			return Response.status(400).entity("Vozilo with given id not found.").build();
		}
		
		Vozilo v = new Vozilo(vtm.id, vtm.marka, vtm.model, vtm.tip, vtm.registracija, vtm.godinaProizvodnje, vtm.uUpotrebi, vtm.napomena, vtm.aktivan);
		
		voziloDAO.getVozilaMap().replace(vtm.id, v);
		voziloDAO.writeVozila(ctx.getRealPath(""));
		
		return Response.status(200).build();
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
