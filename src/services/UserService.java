package services;

import java.util.Collection;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Dostavljac;
import beans.Kupac;
import beans.Porudzbina;
import beans.User;
import beans.UserToLogin;
import beans.UserToModify;
import beans.UserToRegistrate;
import beans.Vozilo;
import dao.ArtikalDAO;
import dao.PorudzbinaDAO;
import dao.RestoranDAO;
import dao.UserDAO;
import dao.VoziloDAO;

@Path("/users")
public class UserService {

	@Context
	HttpServletRequest request;
	@Context
	ServletContext ctx;
	
	
	@GET
	@Path("/getUserlist")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<User> getJustUsers() {
		
		UserDAO userDAO = getUserDAO();
		
		for (User uTemp: userDAO.getUserlist()) {
			System.out.println(uTemp.toString());
		}
		
		
		return getUserDAO().getUserlist();
	}
	
	@GET
	@Path("/getUsermap/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, User> getJustUsermap(@PathParam("username") String username, @PathParam("password") String password) {
		UserDAO userDAO = getUserDAO();
		String res = userDAO.authenticateAdmin(username, password);
		System.out.println("Autheticate response: " + res);
		if (res.equals("OK")) {
			System.out.println("Admin auth successfull.");
			
			return filterMap(userDAO.getUsermap());
		} else {
			System.out.println("Admin auth failed.");
			return null;
		}

	}
	
	private HashMap<String,User> filterMap(HashMap<String, User> usermap) {
		HashMap<String,User> filtered = new HashMap<>();
		
		for (User u: getUserDAO().getUsermap().values()) {
			if (u.isAktivan()) {
				filtered.put(u.getUsername(), u);
			}
		}
		
		
		return filtered;
	}

	@GET
	@Path("/getKupacMap")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, Kupac> getJustKupacMap() {
		return getUserDAO().getKupacmap();
	}
	
	@DELETE
	@Path("/deleteUser/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(@PathParam("username") String username) {
		
		UserDAO userDAO = getUserDAO();
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		
		if(!userDAO.getUsermap().containsKey(username)) {
			return Response.status(400).entity("User with given id not found.").build();
		}
		
		User u = userDAO.getUsermap().get(username);
		u.setAktivan(false);
		
		userDAO.writeUsers(ctx.getRealPath(""));
		
		if (u.getRole() == 0) {
			for (Porudzbina p: porudzbinaDAO.getPorudzbinemap().values()) {
				if (p.getKupacUsername().equals(u.getUsername())) {
					p.setAktivna(false);
				}
			}
		
		}
		
		if (u.getRole() == 1) {
			for (Porudzbina p: porudzbinaDAO.getPorudzbinemap().values()) {
				if (p.getDostavljacUsername().equals(u.getUsername())) {
					p.setStatusPorudzbine(3);
				}
			}
			
			for (Kupac k: userDAO.getKupacmap().values()) {
				for (Porudzbina p: k.getPorudzbine()) {
					if (p.getDostavljacUsername().equals(u.getUsername())) {
						p.setStatusPorudzbine(3);
					}
				}
			}
		}
		
		porudzbinaDAO.writePorudzbine(ctx.getRealPath(""));
		userDAO.writeUsers(ctx.getRealPath(""));
		
		
		return Response.status(200).build();
	}
	
	@POST
	@Path("/modifyUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifyUser(UserToModify utm) {
		
		UserDAO userDAO = getUserDAO();
		
	//	User u = new User(utm.username, utm.password, utm.firstname, utm.lastname, utm.role, utm.phone, utm.email, utm.registrationDate, utm.);
		int oldRole = userDAO.getUsermap().get(utm.oldUsername).getRole();
		String oldEmail = userDAO.getUsermap().get(utm.oldUsername).getEmail();
		
		if(!utm.oldUsername.equals(utm.newUsername)) {
			if (userDAO.getUsermap().containsKey(utm.newUsername)) {
				return Response.status(400).entity("Username exists").build();
			}
		}
		
		if (!oldEmail.equals(utm.email)) {
			if(userDAO.checkDistinct(utm.email))
				return Response.status(400).entity("Email exists").build();
		}
		
		// kupac
		if (utm.role == 0) {
			// modifikacija kupca
			if(userDAO.getKupacmap().containsKey(utm.oldUsername)) {
				Kupac k = replaceKupacFields(userDAO.getKupacmap().get(utm.oldUsername), utm);
				if (utm.oldUsername.equals(utm.newUsername)) {
					userDAO.getUsermap().replace(utm.oldUsername, k);
				} else {
					
					userDAO.getUsermap().remove(utm.oldUsername);
					userDAO.getUsermap().put(utm.newUsername, k);
				}
			} else {
				Kupac k = new Kupac(utm.newUsername, utm.password, utm.firstname, utm.lastname, utm.role, utm.phone, utm.email, utm.registrationDate);
				// izvuci iz dostavljaca
				userDAO.getUsermap().remove(utm.oldUsername);
				if (oldRole == 1) {
					deactivatePorudzbineDostavljac(utm.oldUsername);
					userDAO.getDostavljacmap().remove(utm.oldUsername);
				} 
				
				userDAO.getUsermap().put(utm.newUsername, k);
				userDAO.getKupacmap().put(utm.newUsername, k);
				
				
			}
		} else if (utm.role == 1) {
			if(userDAO.getDostavljacmap().containsKey(utm.oldUsername)) {
				Dostavljac d = replaceDostavljacFields(userDAO.getDostavljacmap().get(utm.oldUsername), utm);
				if (utm.oldUsername.equals(utm.newUsername)) {
					userDAO.getUsermap().replace(utm.oldUsername, d);
				} else {
					userDAO.getUsermap().remove(utm.oldUsername);
					userDAO.getUsermap().put(utm.newUsername, d);
				}
				
			} else {
				Dostavljac d = new Dostavljac(utm.newUsername, utm.password, utm.firstname, utm.lastname, utm.role, utm.phone, utm.email, utm.registrationDate);
				if (oldRole == 0) {
					deactivatePorudzbineKupac(utm.oldUsername);
					userDAO.getKupacmap().remove(utm.oldUsername);
				}
				
				userDAO.getDostavljacmap().put(utm.newUsername, d);
				userDAO.getUsermap().put(utm.newUsername, d);
			}
			
		} else if (utm.role == 2) {
			
			if (oldRole == 0) {
				deactivatePorudzbineKupac(utm.oldUsername);
				userDAO.getKupacmap().remove(utm.oldUsername);
			} else if (oldRole == 1) {
				deactivatePorudzbineDostavljac(utm.oldUsername);
				userDAO.getDostavljacmap().remove(utm.oldUsername);	
			}
			
			User u = new User(utm.newUsername, utm.password, utm.firstname, utm.lastname, utm.role, utm.phone, utm.email, utm.registrationDate, true);
			userDAO.getUsermap().remove(utm.oldUsername);
			userDAO.getUsermap().put(utm.newUsername, u);
		}
		
		
		userDAO.writeUsers(ctx.getRealPath(""));
		getPorudzbinaDAO().writePorudzbine(ctx.getRealPath(""));
		
		return Response.status(200).build();
	}
	
	private void deactivatePorudzbineKupac(String username) {
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		
		for (Porudzbina p: porudzbinaDAO.getPorudzbinemap().values()) {
			if (p.getKupacUsername().equals(username)) {
				p.setAktivna(false);
			}
		}
	}
	
	private void deactivatePorudzbineDostavljac(String username) {
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		
		for (Porudzbina p: porudzbinaDAO.getPorudzbinemap().values()) {
			if (p.getDostavljacUsername().equals(username)) {
				p.setStatusPorudzbine(3);
			}
		}
	}
	
	private Kupac replaceKupacFields(Kupac k, UserToModify utm) {
		k.setUsername(utm.newUsername);
		k.setPassword(utm.password);
		k.setFirstname(utm.firstname);
		k.setLastname(utm.lastname);
		k.setPhone(utm.phone);
		k.setEmail(utm.email);
		k.setBodovi(utm.bodovi);
		
		return k;
	}
	
	private Dostavljac replaceDostavljacFields(Dostavljac d, UserToModify utm) {
		d.setUsername(utm.newUsername);
		d.setPassword(utm.password);
		d.setFirstname(utm.firstname);
		d.setLastname(utm.lastname);
		d.setPhone(utm.phone);
		d.setEmail(utm.email);
		
		
		return d;
	}
	
	
	@GET
	@Path("/zaduziVozilo/{username}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response zaduziVozilo(@PathParam("username") String username, @PathParam("id") int id) {
		
		VoziloDAO voziloDAO = getVoziloDAO();
		UserDAO userDAO = getUserDAO();
		
		if(!voziloDAO.getVozilaMap().containsKey(id)) {
			return Response.status(400).entity("Id vozila nije dobar.").build();
		}
		
		Vozilo v = voziloDAO.getVozilaMap().get(id);
		
		if(v.isuUpotrebi()) {
			return Response.status(400).entity("Vozilo je vec u upotrebi").build();
		}
		
		if(!userDAO.getUsermap().containsKey(username)) {
			return Response.status(400).entity("Username not valid.").build();
		}
		
		
		
		voziloDAO.getVozilaMap().get(id).setuUpotrebi(true);
		
		userDAO.getDostavljacmap().get(username).setVozilo(v);
		
		userDAO.writeUsers(ctx.getRealPath(""));
		voziloDAO.writeVozila(ctx.getRealPath(""));
		
		
		return Response.status(200).build();
	}
	
	@GET
	@Path("/razduziVozilo/{username}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response razduziVozilo(@PathParam("username") String username, @PathParam("id") int id) {
		VoziloDAO voziloDAO = getVoziloDAO();
		UserDAO userDAO = getUserDAO();
		
		if(!voziloDAO.getVozilaMap().containsKey(id)) {
			return Response.status(400).entity("Id vozila nije dobar.").build();
		}
		
		Vozilo v = voziloDAO.getVozilaMap().get(id);
		
		if(!v.isuUpotrebi()) {
			return Response.status(400).entity("Vozilo nije u upotrebi").build();
		}
		
		if(!userDAO.getUsermap().containsKey(username)) {
			return Response.status(400).entity("Username not valid.").build();
		}
		
		
		voziloDAO.getVozilaMap().get(id).setuUpotrebi(false);
		voziloDAO.writeVozila(ctx.getRealPath(""));
		
		userDAO.getDostavljacmap().get(username).setVozilo(null);
		

		userDAO.writeUsers(ctx.getRealPath(""));
		voziloDAO.writeVozila(ctx.getRealPath(""));
		
		
		return Response.status(200).build();
	}
	
	@GET
	@Path("/preuzmiPorudzbinu/{username}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response preuzmiPorudzbinu(@PathParam("username") String username, @PathParam("id") int id) {
		UserDAO userDAO = getUserDAO();
		PorudzbinaDAO porudzbinaDAO = getPorudzbinaDAO();
		
		
		if(!porudzbinaDAO.getPorudzbinemap().containsKey(id)) {
			return Response.status(400).entity("idPorudzbine not valid.").build();
		}
		
		
		
		if(!userDAO.getUsermap().containsKey(username)) {
			return Response.status(400).entity("Username not valid.").build();
		}
		
		Porudzbina p = porudzbinaDAO.getPorudzbinemap().get(id);
		p.setStatusPorudzbine(2);
		p.setDostavljacUsername(username);
		porudzbinaDAO.writePorudzbine(ctx.getRealPath(""));
		

		userDAO.getDostavljacmap().get(username).getPorudzbine().add(p);
		
		Kupac k = (Kupac) getUserDAO().getUsermap().get(p.getKupacUsername());
		
		
		for (int i = 0; i < k.getPorudzbine().size(); i++) {
			if (k.getPorudzbine().get(i).getIdPorudzbine() == id) {
				k.getPorudzbine().get(i).setStatusPorudzbine(2);
			}
		}
		
		userDAO.writeUsers(ctx.getRealPath(""));
		
		return Response.status(200).build();
	}
	
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public User login(UserToLogin u) {
		
		UserDAO userDAO = getUserDAO();
		
		String auth = userDAO.authentication(u.username, u.password);
		
		if (auth.equals("USERNAME NOT FOUND")) {
			System.out.println("Username not found.");
			return new User(1);
			
		} else if (auth.equals("WRONG PASSWORD")) {
			System.out.println("Wrong password.");
			return new User(2);
		} else if (!auth.equals("OK")) {
			System.out.println("Simple ok.");
			
			return null;
		}
		System.out.println("User found.");
		
		User loginUser = userDAO.getByUsername(u.username);
		
		HttpSession session = request.getSession();
		session.setAttribute("user", loginUser);
		
		return loginUser;
		
		
	}
	
	@POST
	@Path("authenticateAdmin")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response authenticateAdmin(UserToLogin u) {
		if (getUserDAO().authenticateAdmin(u.username, u.password).equals("OK")) {
			System.out.println("Admin auth successfull.");
			return Response.status(200).build();
		} else {
			System.out.println("Admin auth failed.");
			return Response.status(403).build();
		}
	}
	
	@GET
	@Path("/logout")
	public void logout() {
		request.getSession().invalidate();
	}
	
	
	@POST
	@Path("/register")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String register(UserToRegistrate u) {
		
		System.out.println("Registrate Rest POST: " + u.username + u.password +  u.firstname + u.lastname + u.phone + u.email + u.registrationDate );
		UserDAO userDAO = getUserDAO();
		// cheking if distinct
		String distinctCheck = userDAO.checkDistinct(u);
		
		if (distinctCheck.equals("OK")) {
			
			String contextPath = ctx.getRealPath("");
			System.out.println("Context Path: " + contextPath);
			
			Kupac newUser = new Kupac(u.username, u.password, u.firstname, u.lastname, 0, u.phone, u.email, u.registrationDate);
			userDAO.getUsermap().put(u.username, newUser);
			userDAO.getKupacmap().put(u.username, newUser);
			userDAO.saveUser(contextPath, newUser);
			
			HttpSession session =  request.getSession();
			session.setAttribute("user", newUser);

		}
		
		return distinctCheck;
	}
	
	
	@GET
	@Path("/getUserOnSession")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserOnSession() {
		HttpSession session = request.getSession();
		User userOnSession = (User) session.getAttribute("user");
		if (userOnSession != null) {
			
			System.out.println("getUserOnSession()šđščščšđčđš: " + userOnSession.toString());
		}
		return userOnSession;
		
	}
	
	@GET
	@Path("/getByUsername/{username}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public User getByUsername(@PathParam("username") String username) {
		System.out.println(username);
		
		return getUserDAO().getByUsername(username);
	}
	
	@GET
	@Path("/getDistinctCheck/{username}/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDistinctCheck(@PathParam("username") String username, @PathParam("email") String email) {
		return getUserDAO().checkDistinct(username, email);
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
