package beans;

import java.util.HashMap;

public class Restoran {
	
	private int id;
	private String naziv;
	private String adresa;
	// 1 - domaca kuhinja; 2 - rostilj; 3 - kinezi; 4 - indija; 5 - poslasticarnica; 6 - picerija
	private int kategorija;
	

	private HashMap<Integer, Artikal> jelaMap = new HashMap<>();
	private HashMap<Integer, Artikal> picaMap = new HashMap<>();
	private String image;
	private String kategorijaStr;
	private boolean aktivan;
	

	public Restoran(int id, String naziv, String adresa, int kategorija, HashMap<Integer, Artikal> jelaMap,
			HashMap<Integer, Artikal> picaMap, String image, boolean aktivan) {
		
		this.id = id;
		this.naziv = naziv;
		this.adresa = adresa;
		this.kategorija = kategorija;
		this.jelaMap = jelaMap;
		this.picaMap = picaMap;
		this.image = image;
		this.kategorijaStr = setKategorijaStr();
		this.aktivan = aktivan;
	}
	
	public Restoran(RestoranToAdd restoranToAdd, int idToAdd) {
		this.id = idToAdd;
		this.naziv = restoranToAdd.naziv;
		this.adresa = restoranToAdd.adresa;
		this.kategorija = restoranToAdd.kategorija;
		this.image = restoranToAdd.image;
		this.kategorijaStr = setKategorijaStr();
		this.aktivan = true;
	}
	
	public Restoran(RestoranToModify restoranToModify) {
		this.id = restoranToModify.id;
		this.naziv = restoranToModify.naziv;
		this.adresa = restoranToModify.adresa;
		this.kategorija = restoranToModify.kategorija;
		this.image = restoranToModify.image;
		this.kategorijaStr = restoranToModify.kategorijaStr;
		this.jelaMap = restoranToModify.jelaMap;
		this.picaMap = restoranToModify.picaMap;
		this.aktivan = true;
		
		//todo maps
	}
	
	private String setKategorijaStr() {
		String retVal = null;
		switch (this.kategorija) {
		case 1:
			retVal = "Domaća kuhinja";
			break;
		case 2:
			retVal = "Roštilj";
			break;
		case 3:
			retVal = "Kineska kuhinja";
			break;
		case 4:
			retVal = "Indijska kuhinja";
			break;
		case 5:
			retVal = "Poslastičarnica";
			break;
		case 6:
			retVal = "Picerija";
			break;

		default:
			break;
		}
		return retVal;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNaziv() {
		return naziv;
	}
	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	public String getAdresa() {
		return adresa;
	}
	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}
	public int getKategorija() {
		return kategorija;
	}
	public void setKategorija(int kategorija) {
		this.kategorija = kategorija;
	}
	public HashMap<Integer, Artikal> getJelaMap() {
		return jelaMap;
	}
	public void setJelaMap(HashMap<Integer, Artikal> jelaMap) {
		this.jelaMap = jelaMap;
	}
	public HashMap<Integer, Artikal> getPicaMap() {
		return picaMap;
	}
	public void setPicaMap(HashMap<Integer, Artikal> picaMap) {
		this.picaMap = picaMap;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	public String getKategorijaStr() {
		return kategorijaStr;
	}

	public void setKategorijaStr(String kategorijaStr) {
		this.kategorijaStr = kategorijaStr;
	}

	public boolean isAktivan() {
		return aktivan;
	}

	public void setAktivan(boolean aktivan) {
		deactivateArtikli();
		this.aktivan = aktivan;
	}

	public void deactivateArtikli() {
		
		for(Artikal a: jelaMap.values()) {
			a.setAktivan(false);
		}
		
		for (Artikal a: picaMap.values()) {
			a.setAktivan(false);
		}
		
		
	}
	
	@Override
	public String toString() {
		return "Restoran [id=" + id + ", naziv=" + naziv + ", adresa=" + adresa + ", kategorija=" + kategorija
				+ ", jelaMap=" + jelaMap + ", picaMap=" + picaMap + ", image=" + image + ", kategorijaStr="
				+ kategorijaStr + "]";
	}

	
	
	
	
	

}
