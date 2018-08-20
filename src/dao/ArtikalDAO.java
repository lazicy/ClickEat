package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import beans.Artikal;

public class ArtikalDAO {


	
	
	HashMap<Integer, Artikal> artikalmap = new HashMap<>();
	
	public ArtikalDAO(String contextPath) {
		loadArtikli(contextPath);
	}

	public HashMap<Integer, Artikal> getArtikalmap() {
		return artikalmap;
	}

	public void setArtikalmap(HashMap<Integer, Artikal> artikalmap) {
		this.artikalmap = artikalmap;
	} 
	
	public Collection<Artikal> getArtikallist() {
		return this.artikalmap.values();
	}
	
	public void deactivateArtikli (int restoranId, Set<Integer> keys) {
		
		Iterator it = keys.iterator();
		while(it.hasNext()) {
			int key = (int) it.next();
			Artikal a = artikalmap.get(key);
			if (a.getRestoranId() == restoranId) {
				a.setAktivan(false);
			}
		}
		
	}
	
	
/*	public void initializeRestorani(RestoranDAO restoranDAO) {
		
		for(Artikal a: getArtikallist()) {
			if (!restoranDAO.getRestoranmap().containsKey(a.getRestoranId())) {
				System.out.println("Initialize restorani: skipped id: + " + a.getRestoranId() + ", for Artikal: " + a.getId());
				continue;
			}
			
			a.setRestoran(restoranDAO.getRestoranmap().get(a.getRestoranId()));
		}
	}*/
	
	
	

	public void writeArtikal(String contextPath, Artikal a) {
	
		try(
				FileWriter fw = new FileWriter(contextPath + "/artikli.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
				out.print(a.getId() + ";" + a.getNaziv() + ";" + a.getTip() + ";" + a.getCena() + ";" + a.getKolicina() + ";" + a.getOpis() + ";"
						+ a.getRestoranId() + ";" + a.isAktivan() + ";" + a.getPopularnost() + ";" + System.getProperty("line.separator"));
			    
			    out.close();
			    bw.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		
	}
	
	
	public void writeArtikli(String contextPath) {
		try(
				FileWriter fw = new FileWriter(contextPath + "/artikli.txt", false);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
				for (Artikal a: getArtikallist()) {
				out.print(a.getId() + ";" + a.getNaziv() + ";" + a.getTip() + ";" + a.getCena() + ";" + a.getKolicina() + ";" + a.getOpis() + 
								";" + a.getRestoranId() + ";" + a.isAktivan() + ";" + a.getPopularnost() + ";" + System.getProperty("line.separator"));
				}
				
			    out.close();
			    bw.close();
			    
			} catch (IOException e) {
			    e.printStackTrace();
			}
	}
	
	
	private void loadArtikli(String contextPath) {
		
		BufferedReader in = null;
		
		
		try {
			File file = new File(contextPath + "/artikli.txt");
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
					String idStr = st.nextToken().trim();
					String naziv= st.nextToken().trim();
					String tipStr = st.nextToken().trim();
					String cenaStr = st.nextToken().trim();
					String kolicinaStr = st.nextToken().trim();
					String opis = st.nextToken().trim();
					String restoranIdStr = st.nextToken().trim();
					String aktivanStr = st.nextToken().trim();
					String popularnostStr = st.nextToken().trim();
					
					int id = -1;
					try {
						id = Integer.parseInt(idStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Id not valid. Continue.");
					}
					
					int tip = -1;
					try {
						tip = Integer.parseInt(tipStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Tip not valid. Continue.");
					}
					
					int popularnost = -1;
					try {
						popularnost = Integer.parseInt(popularnostStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Popularnost not valid. Continue.");
					}
					
					double cena = -1;
					try {
						cena = Double.parseDouble(cenaStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Cena not valid. Continue.");
					}
					
					double kolicina = -1;
					try {
						kolicina = Double.parseDouble(kolicinaStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Kolicina not valid. Continue.");
					}
					
					int restoranId = -1;
					try {
						restoranId = Integer.parseInt(restoranIdStr);
					} catch( NumberFormatException ex) {
						System.out.println("Parent restoran ID not valid. Continue");
					}
					
					boolean aktivan = false;
					if (aktivanStr.equals("true")) {
						aktivan = true;
					}
					
					
					
					
					Artikal a = new Artikal(id, naziv, tip, cena, kolicina, opis, aktivan, restoranId, popularnost);
					//System.out.println("LoadArtikle: " + a.toString());
					artikalmap.put(id, a);
					
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

	
}
