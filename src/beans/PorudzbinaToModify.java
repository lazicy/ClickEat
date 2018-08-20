package beans;

import java.util.Collection;

public class PorudzbinaToModify {
	
	public int idPorudzbine;
	public String kupacUsername;
	public String dostavljacUsername;
	public double ukupnaCena;
	public String datum;
	public int statusPorudzbine;
	public String napomena;
	public Collection<StavkaForPorudzbina> stavke;

}
