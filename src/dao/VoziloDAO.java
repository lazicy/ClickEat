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
import java.util.StringTokenizer;

import beans.Vozilo;

public class VoziloDAO {
	
private HashMap<Integer, Vozilo> vozilaMap = new HashMap<>();

	
	public VoziloDAO(String contextPath) {
		loadVozila(contextPath);
	};
	
	
	public HashMap<Integer, Vozilo> getVozilaMap() {
		return vozilaMap;
	}

	public void setVozila(HashMap<Integer, Vozilo> vozila) {
		this.vozilaMap = vozila;
	}
	
	public Collection<Vozilo> getVozilaList() {
		return this.vozilaMap.values();
	}
	
	
	public void writeVozilo(String contextPath, Vozilo v) {
		try(
				FileWriter fw = new FileWriter(contextPath + "/vozila.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
				writeVoziloLine(out, v);
			    
			    out.close();
			    bw.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
	}
	
	
	public void writeVozila(String contextPath) {
		try(
				FileWriter fw = new FileWriter(contextPath + "/vozila.txt", false);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
				for(Vozilo v: vozilaMap.values()) {
					writeVoziloLine(out, v);
				}
			    
			    out.close();
			    bw.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
	}
	
	private void writeVoziloLine(PrintWriter out, Vozilo v) {
		
		out.print(v.getId() + ";" + v.getMarka() + ";" + v.getModel() + ";" + v.getTip() + ";");
		
		if (v.getTip() == 1) {
			out.print("-;");
		} else {
			out.print(v.getRegistracija() + ";");
		}
		
		out.print(v.getGodinaProizvodnje() + ";" + v.isuUpotrebi()  + ";" + v.getNapomena() + ";" + v.isAktivan() + ";" + System.getProperty("line.separator"));
		
	}
	
	private void loadVozila(String contextPath) {
		
		BufferedReader in = null;
		
		
		try {
			File file = new File(contextPath + "/vozila.txt");
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
					String marka= st.nextToken().trim();
					String model = st.nextToken().trim();
					String tipStr = st.nextToken().trim();
					String registracija = st.nextToken().trim();
					String godinaProizvodnjeStr = st.nextToken().trim();
					String uUpotrebiStr = st.nextToken().trim();
					String napomena = st.nextToken().trim();
					String aktivanStr = st.nextToken().trim();
					
					int id = -1;
					try {
						id = Integer.parseInt(idStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Id not valid. Continue.");
						continue;
					}
					
					int tip = -1;
					try {
						tip = Integer.parseInt(tipStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Tip not valid. Continue.");
						continue;
					}
					
					if (tip != 1 && tip != 2 && tip !=3) {
						System.out.println("Tip overflow.");
						continue;
					}
					
					int godinaProizvodnje = -1;
					try {
						godinaProizvodnje = Integer.parseInt(godinaProizvodnjeStr);
						
					} catch (NumberFormatException ex) {
						System.out.println("Godina proizvodnje not valid.");
					}
					
					boolean uUpotrebi = false;
					if (uUpotrebiStr.equals("true")) {
						uUpotrebi = true;
					}
					
					boolean aktivan = false;
					if (aktivanStr.equals("true")) {
						aktivan = true;
					}
					
					if(tip == 1) {
						registracija = "-";
					}
					
					Vozilo v = new Vozilo(id, marka, model, tip, registracija, godinaProizvodnje, uUpotrebi, napomena, aktivan);
					vozilaMap.put(id, v);
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
