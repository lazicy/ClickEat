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

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import beans.Artikal;
import beans.Porudzbina;
import beans.StavkaPorudzbine;

public class PorudzbinaDAO {
	
	@Context
	ServletContext ctx;
	
	private HashMap<Integer, Porudzbina> porudzbinemap = new HashMap<>();

	
	public PorudzbinaDAO(String contextPath, ArtikalDAO artikalDAO) {
		loadPorudzbine(contextPath, artikalDAO);
	}
	
	public HashMap<Integer, Porudzbina> getPorudzbinemap() {
		return porudzbinemap;
	}

	public void setPorudzbinemap(HashMap<Integer, Porudzbina> porudzbinemap) {
		this.porudzbinemap = porudzbinemap;
	}
	
	public Collection<Porudzbina> getPorudzbinalist() {
		return this.porudzbinemap.values();
	}
	
	// ne cuvaju se u fajl, vec samo cuvaju useru trenutne stavke u korpi
	public HashMap<String, HashMap<Integer, StavkaPorudzbine>> trenutneStavke = new HashMap<>();
	
	

	public HashMap<String, HashMap<Integer, StavkaPorudzbine>> getTrenutneStavke() {
		return trenutneStavke;
	}

	public void setTrenutneStavke(HashMap<String, HashMap<Integer, StavkaPorudzbine>> trenutneStavke) {
		this.trenutneStavke = trenutneStavke;
	}

	public void writePorudzbina(String contextPath, Porudzbina p) {
		
		try(
				FileWriter fw = new FileWriter(contextPath + "/porudzbine.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
				writePorudzbinaLine(out, p);
			    
			    out.close();
			    bw.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		
	}
	
	public void writePorudzbine(String contextPath) {
		
		try(
				FileWriter fw = new FileWriter(contextPath + "/porudzbine.txt", false);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
				for (Porudzbina p: getPorudzbinalist()) {
					writePorudzbinaLine(out, p);
				}
			    
			    out.close();
			    bw.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
	}
	
	private void writePorudzbinaLine(PrintWriter out, Porudzbina p) {
		
		out.print(p.getIdPorudzbine() + ";");
		
		ArrayList<StavkaPorudzbine> stavke = p.getStavkePorudzbine();
		
		for(int i = 0; i < stavke.size(); i++) {
			out.print(stavke.get(i).getArtikal().getId() + "#" + stavke.get(i).getBrojPorcija());
			if (i != stavke.size()-1) {
				out.print(",");
			}
		}
		
		if (p.getNapomena() == null) {
			p.setNapomena("-");
		}
		
		out.print(";");
		out.print(p.getUkupnaCena() + ";" + p.getDatum() + ";" + p.getKupacUsername() + ";" + p.getDostavljacUsername() + ";" 
					+ p.getStatusPorudzbine() + ";" + p.getNapomena() + ";" + p.isAktivna() + ";" +
												System.getProperty("line.separator"));
		
	}

	private void loadPorudzbine(String contextPath, ArtikalDAO artikalDAO) {
		
		BufferedReader in = null;
		
		try {
			File file = new File(contextPath + "/porudzbine.txt");
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
					String stavkaStr = st.nextToken().trim();
					String ukupnaCenaStr = st.nextToken().trim();
					String datum = st.nextToken().trim();
					String kupacStr = st.nextToken().trim();
					String dostavljacStr = st.nextToken().trim();
					String statusStr = st.nextToken().trim();
					String napomena = st.nextToken().trim();
					String aktivnaStr = st.nextToken().trim();
					
					int id = -1;
					try {
						id = Integer.parseInt(idStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Id not valid. Continue.");
						continue;
					}
					
					double ukupnaCena = -1;
					try {
						ukupnaCena = Double.parseDouble(ukupnaCenaStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Ukupna cena not valid. Continue.");
						continue;
					}
					
					int status = -1;
					try {
						status = Integer.parseInt(statusStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Status not valid. Continue.");
						continue;
					}
					
					boolean aktivna = false;
					if (aktivnaStr.equals("true")) {
						aktivna = true;
					}
					
					if (!stavkaStr.contains("#")) {
						System.out.println("Stavka format not valid. Continue.");
						continue;
					}
					
					// parsiranje stavke
					String[] stavke = stavkaStr.split(",");
					ArrayList<StavkaPorudzbine> stavkeForAdd = new ArrayList<>();
					for (String stStavka: stavke) {

						String[] stavka = stStavka.split("#");
						String artikalStr;
						String porcijaStr;
						
						artikalStr = stavka[0];
						porcijaStr = stavka[1];
						
						int artikalId = -1;
						try {
							artikalId = Integer.parseInt(artikalStr);
							
						} catch (NumberFormatException ex) {
							System.out.println("Artikal ID not valid. Continue.");
							continue;
						}
						
						int porcija = -1;
						try {
							porcija = Integer.parseInt(porcijaStr);
							
						} catch (NumberFormatException ex) {
							System.out.println("Porcija not valid. Continue.");
							continue;
						}
						
						Artikal a = artikalDAO.getArtikalmap().get(artikalId);
						if (a == null) {
							System.out.println("Artikal not found");
							continue;
						}
						
						StavkaPorudzbine stavkaPorudzbine = new StavkaPorudzbine(a, porcija);
						System.out.println("Stavka porudzbine: " + stavkaPorudzbine);
						stavkeForAdd.add(stavkaPorudzbine);
					}
					
							
					
					Porudzbina p = new Porudzbina(id, stavkeForAdd, ukupnaCena, datum, kupacStr, dostavljacStr, status, napomena, aktivna);
					System.out.println("Adding porudzbina: " + p.toString());
					porudzbinemap.put(id, p);
					
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
