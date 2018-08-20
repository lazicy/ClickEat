package beans;

import java.util.ArrayList;

public class Porudzbina {
	
	private int idPorudzbine;
	private ArrayList<StavkaPorudzbine> stavkePorudzbine;
	private String datum;
	private double ukupnaCena;
	private String kupacUsername;
	private String dostavljacUsername;
	// 1 - poruceno; 2 - dostava u toku; 3 - otkazano; 4 - dostavljeno
	private int statusPorudzbine;
	private String napomena;
	private boolean aktivna;
	
	
	public Porudzbina(int idPorudzbine, ArrayList<StavkaPorudzbine> stavkePorudzbine, double ukupnaCena, String datum, String kupacUsername, String dostavljacUsername,
			int statusPorudzbine, String napomena, boolean aktivna) {
		this.idPorudzbine = idPorudzbine;
		this.stavkePorudzbine = stavkePorudzbine;
		this.ukupnaCena = ukupnaCena;
		this.datum = datum;
		this.kupacUsername = kupacUsername;
		this.dostavljacUsername = dostavljacUsername;
		this.statusPorudzbine = statusPorudzbine;
		this.napomena = napomena;
		this.aktivna = aktivna;
	}
	
	public Porudzbina(int idPorudzbine, ArrayList<StavkaPorudzbine> stavkePorudzbine, double ukupnaCena, String datum, String kupacUsername, String dostavljacUsername,
			int statusPorudzbine, String napomena) {
		this.idPorudzbine = idPorudzbine;
		this.stavkePorudzbine = stavkePorudzbine;
		this.ukupnaCena = ukupnaCena;
		this.datum = datum;
		this.kupacUsername = kupacUsername;
		this.dostavljacUsername = dostavljacUsername;
		this.statusPorudzbine = statusPorudzbine;
		this.napomena = napomena;
		this.aktivna = true;
	}
	
	
	

	public int getIdPorudzbine() {
		return idPorudzbine;
	}

	public void setIdPorudzbine(int idPorudzbine) {
		this.idPorudzbine = idPorudzbine;
	}

	

	public ArrayList<StavkaPorudzbine> getStavkePorudzbine() {
		return stavkePorudzbine;
	}


	public void setStavkePorudzbine(ArrayList<StavkaPorudzbine> stavkePorudzbine) {
		this.stavkePorudzbine = stavkePorudzbine;
	}


	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public String getKupacUsername() {
		return kupacUsername;
	}

	public void setKupacUsername(String kupacUsername) {
		this.kupacUsername = kupacUsername;
	}

	public String getDostavljacUsername() {
		return dostavljacUsername;
	}

	public void setDostavljacUsername(String dostavljacUsername) {
		this.dostavljacUsername = dostavljacUsername;
	}

	public int getStatusPorudzbine() {
		return statusPorudzbine;
	}

	public void setStatusPorudzbine(int statusPorudzbine) {
		this.statusPorudzbine = statusPorudzbine;
	}

	public String getNapomena() {
		return napomena;
	}

	public void setNapomena(String napomena) {
		this.napomena = napomena;
	}

	public double getUkupnaCena() {
		return ukupnaCena;
	}


	public void setUkupnaCena(double ukupnaCena) {
		this.ukupnaCena = ukupnaCena;
	}

	

	public boolean isAktivna() {
		return aktivna;
	}


	public void setAktivna(boolean aktivna) {
		this.aktivna = aktivna;
	}


	@Override
	public String toString() {
		return "Porudzbina [idPorudzbine=" + idPorudzbine + ", stavkePorudzbine=" + stavkePorudzbine + ", datum="
				+ datum + ", kupacUsername=" + kupacUsername + ", dostavljacUsername=" + dostavljacUsername
				+ ", statusPorudzbine=" + statusPorudzbine + ", napomena=" + napomena + "]";
	}


	
	

}
