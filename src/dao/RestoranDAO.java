package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import com.fasterxml.jackson.annotation.JsonBackReference;

import beans.Artikal;
import beans.Restoran;

public class RestoranDAO {
	
	@Context
	ServletContext ctx;
	
	private HashMap<Integer, Restoran> restoranmap = new HashMap<>();
	
	public Restoran getRestoranById(int id) {
		if (!restoranmap.containsKey(id)) {
			return null;
		}
		return restoranmap.get(id);
		
	}
	
	
	public RestoranDAO(String contextPath, ArtikalDAO artikli) {
		loadRestorani(contextPath, artikli);
	}
	
	
	public HashMap<Integer, Restoran> getRestoranmap() {
		return restoranmap;
	}

	public void setRestoranmap(HashMap<Integer, Restoran> restoranmap) {
		this.restoranmap = restoranmap;
	}

	public Collection<Restoran> getRestoranlist() {
		return this.restoranmap.values();
	}
	
	public void deactivateArtikal(int restoranId, Artikal a) {
		Restoran r = restoranmap.get(restoranId);
		System.out.println("Artikal " + a.getId() + ", tip: " + a.getTip());
		if (a.getTip() == 1) {
			r.getJelaMap().get(a.getId()).setAktivan(false);
			System.out.println("U restoranu " + r.getNaziv() + " artikal " + a.getId() + ", " + a.getNaziv() + " setAktivan to false.");
		} else if (a.getTip() == 2) {
			r.getPicaMap().get(a.getId()).setAktivan(false);
			System.out.println("U restoranu " + r.getNaziv() + " artikal " + a.getId() + ", " + a.getNaziv() + " setAktivan to false.");
		}
	}
	
	
	public boolean replaceRestoran(String contextPath, Restoran r) throws IOException {
		boolean retVal = false;
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		PrintWriter out = null;
		
		File inputFile = null;
		File tempFile = null;
		
		FileInputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		FileWriter fileWriter = null;
		
		try {
			inputFile = new File(contextPath +  "/restorani.txt");
			tempFile = new File(contextPath + "/tmp.txt");
			inputStream = new FileInputStream(inputFile);
			inputStreamReader = new InputStreamReader(inputStream, "UTF8");
			fileWriter = new FileWriter(tempFile);
			reader = new BufferedReader( inputStreamReader);
			writer = new BufferedWriter(fileWriter);
			out = new PrintWriter(writer);
			
		    String line;
			StringTokenizer st;
			
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					String idStr = st.nextToken().trim();
					
					int id = -1;
					try {
						id = Integer.parseInt(idStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Id not valid. Continue.");
					}
					
					if (id == -1)
						continue;
					
					if (id == r.getId()) {
						System.out.println("LINE FOUND: " + line);
						// todo artikli
						out.println("\n" + r.getId() + ";" + r.getNaziv() + ";" + r.getAdresa() + ";" + r.getKategorija()  + ";" + "-" + ";" + "-" + ";" + r.getImage());
						break;
						
					}
					
					System.out.println("LINE NOT FOUND: " + line);
					
					out.write(line + System.getProperty("line.separator"));
					break;
				}
			}
		    
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		inputStreamReader.close();
		inputStream.close();
		reader.close();
		writer.close();
		out.close();
		fileWriter.close();
		
		
		Files.move(tempFile.toPath(), inputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		
		
		
		return retVal;
	}
	
	public void writeRestorani(String contextPath) {
		
		try(
				FileWriter fw = new FileWriter(contextPath + "/restorani.txt", false);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{	
				for (Restoran r: getRestoranlist()) {
					writeRestoranLine(out, r);
				}
			    
			    out.close();
			    bw.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		
	}
	
	
	public void writeRestoran(String contextPath, Restoran r) {
	
		try(
				FileWriter fw = new FileWriter(contextPath + "/restorani.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    writeRestoranLine(out, r);
			    
			    out.close();
			    bw.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		
	}
	
	private void writeRestoranLine(PrintWriter out, Restoran r) {
		 out.print(r.getId() + ";" + r.getNaziv() + ";" + r.getAdresa() + ";" + r.getKategorija()  + ";");
		    
		    // print JelaMap
		    if (!r.getJelaMap().isEmpty()) {
		    
			    Set<Integer> jela = r.getJelaMap().keySet();
			    Iterator it = jela.iterator();
			   
			    while (it.hasNext()) {
			    	out.print(it.next());
			    	if (it.hasNext()) {
			    		out.print(","); 
			    	}
			    }
			} else {
				out.print("-");
			}
		    
		    out.print(";");
		    
		    
		    // print PicaMap
		    if (!r.getPicaMap().isEmpty()) {
			    
			    Set<Integer> pica = r.getPicaMap().keySet();
			    Iterator it = pica.iterator();
			   
			    while (it.hasNext()) {
			    	out.print(it.next());
			    	if (it.hasNext()) {
			    		out.print(","); 
			    	}
			    }
			} else {
				out.print("-");
			}
		    
		    
		    out.print(";");
		    
		    
		    // print image i status
		    out.print(r.getImage() + ";" + r.isAktivan() + ";" + System.getProperty("line.separator"));
		
	}

	private void loadRestorani(String contextPath, ArtikalDAO artikli) {
		BufferedReader in = null;
		
		
		
		try {
			File file = new File(contextPath + "/restorani.txt");
			in = new BufferedReader( new InputStreamReader(
                    new FileInputStream(file), "UTF8"));
			String line;
			StringTokenizer st, stJela, stPica;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					String idStr = st.nextToken().trim();
					String naziv= st.nextToken().trim();
					String adresa = st.nextToken().trim();
					String kategorijaStr = st.nextToken().trim();
					String jelaStr = st.nextToken().trim();
					String picaStr = st.nextToken().trim();
					String image = st.nextToken().trim();
					String aktivanStr = st.nextToken().trim();
					
					int id = -1;
					try {
						id = Integer.parseInt(idStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Id not valid. Continue.");
					}
					
					
					int kategorija = -1;
					try {
						kategorija = Integer.parseInt(kategorijaStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Kategorija not valid. Continue.");
					}
					
					HashMap<Integer, Artikal> jelaMapLocal = new HashMap<>();
					HashMap<Integer, Artikal> picaMapLocal = new HashMap<>();
					
					// iscitavanje ID jela koji su odvojeni zarezom, nakon toga konverzija u int i dobavljanje iz mape
					// svih artikala i ukoliko je pronadjen, dodavanje u mapu artikala restorana
					stJela = new StringTokenizer(jelaStr, ",");
					String jeloStr;
					while (stJela.hasMoreTokens()) {
						jeloStr = stJela.nextToken();
						
						int jeloId = -1;
						try {
							jeloId = Integer.parseInt(jeloStr);
						} catch (NumberFormatException ex) {
							System.out.println("Jelo ID not valid. Continue.");
							continue;
						}
						
						if (jeloId == -1)
							continue;
						
						System.out.println("JeloID: " + jeloId + " read.");
						
						Artikal jeloForAdd = artikli.getArtikalmap().get(jeloId);
						if (jeloForAdd != null) {
							jelaMapLocal.put(jeloId, jeloForAdd);
						}
						
					}
					
					stPica = new StringTokenizer(picaStr, ",");
					String piceStr;
					
					while (stPica.hasMoreTokens()) {
						piceStr = stPica.nextToken();
						
						int piceId = -1;
						try {
							piceId = Integer.parseInt(piceStr);
						} catch(NumberFormatException ex) {
							System.out.println("Pice ID not valid. Continue.");
							continue;
						}
						
						if (piceId == -1)
							continue;
						
						System.out.println("PiceID: " + piceId + " read.");
						
						Artikal piceForAdd = artikli.getArtikalmap().get(piceId);
						if (piceForAdd != null) {
							picaMapLocal.put(piceId, piceForAdd);
						}
					
					}
					
					boolean aktivan = false;
					if (aktivanStr.equals("true")) {
						aktivan = true;
					}
					
					Restoran r = new Restoran(id, naziv, adresa, kategorija, jelaMapLocal, picaMapLocal, image, aktivan);
					//System.out.println("LoadRestorani: " + r.toString());
					restoranmap.put(id, r);
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
