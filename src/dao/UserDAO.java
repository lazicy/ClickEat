package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Dostavljac;
import beans.Kupac;
import beans.Porudzbina;
import beans.Restoran;
import beans.User;
import beans.UserToRegistrate;
import beans.Vozilo;

public class UserDAO {
	
	// mapa svih korisnika sa njihovim informacijama
	HashMap<String, User> usermap = new HashMap<>();
	
	// mapa kupaca
	HashMap<String, Kupac> kupacmap = new HashMap<>();
	
	//mapa dostavljaca
	HashMap<String, Dostavljac> dostavljacmap = new HashMap<>();
	
	public UserDAO() {
		
	}
	
	public UserDAO(String contextPath, PorudzbinaDAO porudzbine, RestoranDAO restorani, VoziloDAO vozila) {
		loadUsers(contextPath, porudzbine, restorani, vozila);
	}
	
	public HashMap<String, User> getUsermap() {
		return this.usermap;
	}
	
	public HashMap<String, Kupac> getKupacmap() {
		return this.kupacmap;
	}
	
	public HashMap<String, Dostavljac> getDostavljacmap() {
		return this.dostavljacmap;
	}
	
	
	public Collection<User> getUserlist() {
		return this.usermap.values();
	}
	
	
	public void saveUser(String contextPath, User u) {
		writeUser(contextPath, u);
	}
	
	
	public String authentication(String username, String password) {
		System.out.println("Username auth: " + username);
		System.out.println(usermap.get(username));
		
		if (!usermap.containsKey(username)) {
			return "USERNAME NOT FOUND";
		}
		User user = usermap.get(username);
		if (!user.getPassword().equals(password)) {
			return "WRONG PASSWORD";
		}
		return "OK";
	}
	
	public String authenticateAdmin(String username, String password) {
		
		String res = authentication(username, password);
		System.out.println("Authentication response: " + res);
		
		if (res.equals("OK")) {
			if (usermap.get(username).getRole() == 2) {
				return "OK";
			}
		}
		
		return "FORBIDDEN";
	}
	
	public User getByUsername(String username) {
		if (!usermap.containsKey(username)) {
			return null;
		}
		return usermap.get(username);
		
	}
	
	// checkin if distinct my username (first) and by email
	public String checkDistinct(UserToRegistrate u) {
		
		
		if (usermap.containsKey(u.username)) {
			return "USERNAME";
		}
		
		ArrayList<User> userlist = new ArrayList<>(getUserlist());
		for (int i=0; i < userlist.size(); i++) {
			if (userlist.get(i).getEmail().equals(u.email)) {
				return "EMAIL";
			}
		}
		
		return "OK";
		
	}
	
	public String checkDistinct(String username, String email) {
		
		if (usermap.containsKey(username)) {
			return "USERNAME";
		}
		
		ArrayList<User> userlist = new ArrayList<>(getUserlist());
		for (int i=0; i < userlist.size(); i++) {
			if (userlist.get(i).getEmail().equals(email)) {
				return "EMAIL";
			}
		}
		
		return "OK";
		
	}
	
	public boolean checkDistinct(String email) {
		ArrayList<User> userlist = new ArrayList<>(getUserlist());
		for (int i=0; i < userlist.size(); i++) {
			if (userlist.get(i).getEmail().equals(email)) {
				return false;
			}
		}
		
		return true;
	}
	
	

	private void writeUser(String contextPath, User u) {
		try(
				FileWriter fw = new FileWriter(contextPath + "/users.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
		
				
			{
				int role = u.getRole();
				
				switch (role) {
				case 0:
					writeKupacLine(out, (Kupac) u);
					break;

				case 1:
					writeDostavljacLine(out, (Dostavljac) u);
					break;
				case 2:
					writeUserLine(out, u);
					break;
				default:
					break;
				}
				
			    out.close();
			    bw.close();
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
		
		
		
	}
	
	
	public void writeUsers(String contextPath) {
		try(
				FileWriter fw = new FileWriter(contextPath + "/users.txt", false);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
		
				
			{
				for(User u: usermap.values()) {
						
					int role = u.getRole();
					
					switch (role) {
					case 0:
						writeKupacLine(out, (Kupac) u);
						break;
	
					case 1:
						writeDostavljacLine(out, (Dostavljac) u);
						break;
					case 2:
						writeUserLine(out, u);
						break;
					default:
						break;
					}
			}
				
			    out.close();
			    bw.close();
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
		
	}
	
	private void writeUserLine(PrintWriter out, User u) {
		 out.print(u.getUsername() + ";" + u.getPassword() + ";" + u.getFirstname() + ";" + u.getLastname() + ";" + u.getRole() + ";" + 
				 	u.getPhone() + ";" + u.getEmail()  + ";" + u.getRegistrationDate() + ";-;-;-;-;-;"  + u.isAktivan() +";" +
				 	System.getProperty("line.separator"));
		    
	}
	
	private void writeKupacLine(PrintWriter out, Kupac k) {
		out.print(k.getUsername() + ";" + k.getPassword() + ";" + k.getFirstname() + ";" + k.getLastname() + ";" + k.getRole() + ";" + 
				 	k.getPhone() + ";" + k.getEmail()  + ";" + k.getRegistrationDate() + ";" );
		
		
		if (k.getPorudzbine().isEmpty()) {
			out.print("-");
		}
		
		for (int i = 0; i < k.getPorudzbine().size(); i++){
			out.print(k.getPorudzbine().get(i).getIdPorudzbine());
			if (i != k.getPorudzbine().size()-1 ) {
				out.print(",");
			}
		}
		
		out.print(";");
		
		if (k.getFavRestorani().isEmpty()) {
			out.print("-");
		}
		
		for (int i = 0; i < k.getFavRestorani().size(); i++){
			out.print(k.getFavRestorani().get(i).getId());
			if (i != k.getFavRestorani().size()-1 ) {
				out.print(",");
			}
		}
		

		out.print(";-;-;" + k.getBodovi() + ";"  + k.isAktivan() +";" + System.getProperty("line.separator"));
	}
	
	private void writeDostavljacLine(PrintWriter out, Dostavljac d) {
		out.print(d.getUsername() + ";" + d.getPassword() + ";" + d.getFirstname() + ";" + d.getLastname() + ";" + d.getRole() + ";" + 
			 	d.getPhone() + ";" + d.getEmail()  + ";" + d.getRegistrationDate() + ";-;-;");
		
		if(d.getPorudzbine().isEmpty()) {
			out.print("-");
		}
		
		for (int i = 0; i < d.getPorudzbine().size(); i++){
			out.print(d.getPorudzbine().get(i).getIdPorudzbine());
			if (i != d.getPorudzbine().size()-1 ) {
				out.print(",");
			}
		}
		
		out.print(";");
		
		if (d.getVozilo() == null) {
			out.print("-");
		} else {
			out.print(d.getVozilo().getId());
		}
		
		out.print(";-;" + d.isAktivan() +";" + System.getProperty("line.separator"));
		
		
	}
	
	private void loadUsers(String contextPath, PorudzbinaDAO porudzbine, RestoranDAO restorani, VoziloDAO vozila) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/users.txt");
			in = new BufferedReader( new InputStreamReader(
                    new FileInputStream(file), "UTF8"));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					
					String username = st.nextToken().trim();
					String password = st.nextToken().trim();
					String firstname = st.nextToken().trim();
					String lastname = st.nextToken().trim();
					String roleStr = st.nextToken().trim();
					String phoneStr = st.nextToken().trim();
					String email = st.nextToken().trim();
					String registrationDateStr = st.nextToken().trim();
					String kupacPorStr = st.nextToken().trim();
					String kupacFavStr = st.nextToken().trim();
					String dostavljacPorStr = st.nextToken().trim();
					String voziloIdStr = st.nextToken().trim();
					String bodoviStr = st.nextToken().trim();
					String aktivanStr = st.nextToken().trim();

					int role = -1;
					try {
						role = Integer.parseInt(roleStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Role not vaild. Continue.");
					}
					
					int bodovi = -1;
					try {
						bodovi = Integer.parseInt(bodoviStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Bodovi not vaild. Continue.");
					}
					
					int voziloId = -1;
					try {
						voziloId = Integer.parseInt(voziloIdStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("VoziloID not vaild. Continue.");
					}
					
					ArrayList<Porudzbina> kupacPorudzbine = null;
					ArrayList<Restoran> kupacFavRestorani = null;
					if (role == 0) {
						kupacPorudzbine = loadPorudzbineById(kupacPorStr, porudzbine, username);
						kupacFavRestorani = loadRestoraniById(kupacFavStr, restorani);
					}
					Vozilo v = null;
					ArrayList<Porudzbina> dostavljacPorudzbine = null;
					if (role == 1) {
						dostavljacPorudzbine = loadPorudzbineById(dostavljacPorStr, porudzbine, username);
						
						for( Porudzbina p: dostavljacPorudzbine) {
							System.out.println("READDostavljac porudzbina: " + p.getIdPorudzbine());
						}
						
						v = vozila.getVozilaMap().get(voziloId);
						if (v == null) {
							System.out.println("Vozilo failed.");
						}
					}
					
					
					boolean aktivan = false;
					if (aktivanStr.equals("true")) {
						aktivan = true;
					}
					
					switch (role) {
					case 0:
						Kupac k = new Kupac(username, password, firstname, lastname, role, phoneStr, email, registrationDateStr, kupacPorudzbine, kupacFavRestorani, bodovi, aktivan);
						usermap.put(username, k);
						kupacmap.put(username, k);
						break;

					case 1:
						Dostavljac d = new Dostavljac(username, password, firstname, lastname, role, phoneStr, email, registrationDateStr, dostavljacPorudzbine, v, aktivan);
						usermap.put(username, d);
						dostavljacmap.put(username, d);
						break;
					case 2:
						User u = new User(username, password, firstname, lastname, role, phoneStr, email, registrationDateStr, aktivan);
						usermap.put(username, u);
						break;
					default:
						break;
					}
					
					
					
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
	}
	
	private ArrayList<Porudzbina> loadPorudzbineById(String ids, PorudzbinaDAO porudzbine, String username) {
		
		ArrayList<Porudzbina> retVal = new ArrayList<>();
		if (ids.equals("-"))
			return retVal;
		
		String[] idTokens = ids.split(",");
		
		for (String idStr: idTokens) {
			boolean skip = false;
			int id = -1;
			try {
				id = Integer.parseInt(idStr);
				
			} catch (NumberFormatException ex) {
				System.out.println("Id porudzbine not valid. Continue.");
				continue;
			
			}
			
			for(int i = 0; i < retVal.size(); i++) {
				if (retVal.get(i).getIdPorudzbine() == id) {
					skip = true;;
				}
			}
			
			Porudzbina p = porudzbine.getPorudzbinemap().get(id);
			System.out.println("PORUDZBI");
			
			
			
			if (p == null || skip == true)
				continue;
			
			retVal.add(p);
			
		}
		
		return retVal;
		
	}
	
	private ArrayList<Restoran> loadRestoraniById(String ids, RestoranDAO restorani) {
		
		ArrayList<Restoran> retVal = new ArrayList<>();
		if (ids.equals("-"))
			return retVal;

		String[] idTokens = ids.split(",");
		
		for (String idStr: idTokens) {
			int id = -1;
			try {
				id = Integer.parseInt(idStr);
				
			} catch (NumberFormatException ex) {
				System.out.println("Id restorana not valid. Continue.");
				continue;
			
			}
			
			Restoran r = restorani.getRestoranmap().get(id);
			if (r != null)
				retVal.add(r);
			
		}
		
		return retVal;
	}
	
	

	

}
