package beans;

import java.util.HashMap;

public class RestoranToModify {
	
	public int id;
	public String naziv;
	public String adresa;
	public int kategorija;
	public HashMap<Integer, Artikal> jelaMap = new HashMap<>();
	public HashMap<Integer, Artikal> picaMap = new HashMap<>();
	public String image;
	public String kategorijaStr;
	public boolean aktivan;

}
