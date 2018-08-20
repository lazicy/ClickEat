package beans;


public class Artikal {
	
	private int id;
	private String naziv;
	// 1 - jelo; 2 - pice
	private int tip;
	private double cena;
	private double kolicina;
	private String opis;
	private boolean aktivan;
	private int restoranId;
	private int popularnost;
	
	public Artikal(int id, String naziv, int tip, double cena, double kolicina, String opis, boolean aktivan, int restoranId, int popularnost) {
		this.id = id;
		this.naziv = naziv;
		this.tip = tip;
		this.cena = cena;
		this.kolicina = kolicina;
		this.opis = opis;
		this.aktivan = aktivan;
		this.restoranId = restoranId;
		this.popularnost = popularnost;
	}
	
	public Artikal() {
		
	}
	
	public Artikal(ArtikalToAdd artikalToAdd, int idToAdd) {
		this.id = idToAdd;
		this.naziv = artikalToAdd.naziv;
		this.tip = artikalToAdd.tip;
		this.cena = artikalToAdd.cena;
		this.kolicina = artikalToAdd.kolicina;
		this.opis = artikalToAdd.opis;
		this.aktivan = true;
		this.restoranId = artikalToAdd.restoranId;
		this.popularnost = 0;
		
	}
	
	public Artikal(ArtikalToModify artikalToModify) {
		this.id = artikalToModify.id;
		this.naziv = artikalToModify.naziv;
		this.tip = artikalToModify.tip;
		this.cena = artikalToModify.cena;
		this.kolicina = artikalToModify.kolicina;
		this.opis = artikalToModify.opis;
		this.aktivan = artikalToModify.aktivan;
		this.restoranId = artikalToModify.restoranId;
		this.popularnost = artikalToModify.popularnost;
		
	}
	
	public int getTip() {
		return tip;
	}
	public void setTip(int tip) {
		this.tip = tip;
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
	public double getCena() {
		return cena;
	}
	public void setCena(double cena) {
		this.cena = cena;
	}
	public String getOpis() {
		return opis;
	}
	public void setOpis(String opis) {
		this.opis = opis;
	}
	public double getKolicina() {
		return kolicina;
	}
	public void setKolicina(double kolicina) {
		this.kolicina = kolicina;
	}
	

	public boolean isAktivan() {
		return aktivan;
	}

	public void setAktivan(boolean aktivan) {
		this.aktivan = aktivan;
	}
	

	public int getRestoranId() {
		return restoranId;
	}

	public void setRestoranId(int restoranId) {
		this.restoranId = restoranId;
	}
	
	

	public int getPopularnost() {
		return popularnost;
	}

	public void setPopularnost(int popularnost) {
		this.popularnost = popularnost;
	}

	@Override
	public String toString() {
		return "Artikal [id=" + id + ", naziv=" + naziv + ", tip=" + tip + ", cena=" + cena + ", kolicina=" + kolicina
				+ ", opis=" + opis + ", aktivan=" + aktivan + ", restoranId=" + restoranId + "]";
	}

	

	
}
