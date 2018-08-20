package beans;

public class Vozilo {
	
	private int id;
	private String marka;
	private String model;
	// 1 - bicikl; 2 - skuter; 3 - automobil
	private int tip;
	private String registracija;
	private int godinaProizvodnje;
	private boolean uUpotrebi;
	private String napomena;
	private boolean aktivan;
	
	
	public Vozilo(int id, String marka, String model, int tip, String registracija, int godinaProizvodnje, boolean uUpotrebi,
			String napomena, boolean aktivan) {
		this.id = id;
		this.marka = marka;
		this.model = model;
		this.tip = tip;
		this.registracija = registracija;
		this.godinaProizvodnje = godinaProizvodnje;
		this.uUpotrebi = uUpotrebi;
		this.napomena = napomena;
		this.aktivan = aktivan;
	}
	
	

	

	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getMarka() {
		return marka;
	}


	public void setMarka(String marka) {
		this.marka = marka;
	}


	public String getModel() {
		return model;
	}


	public void setModel(String model) {
		this.model = model;
	}


	public int getTip() {
		return tip;
	}


	public void setTip(int tip) {
		this.tip = tip;
	}


	public String getRegistracija() {
		return registracija;
	}


	public void setRegistracija(String registracija) {
		this.registracija = registracija;
	}


	public int getGodinaProizvodnje() {
		return godinaProizvodnje;
	}


	public void setGodinaProizvodnje(int godinaProizvodnje) {
		this.godinaProizvodnje = godinaProizvodnje;
	}


	public boolean isuUpotrebi() {
		return uUpotrebi;
	}


	public void setuUpotrebi(boolean uUpotrebi) {
		this.uUpotrebi = uUpotrebi;
	}


	public String getNapomena() {
		return napomena;
	}


	public void setNapomena(String napomena) {
		this.napomena = napomena;
	}


	public boolean isAktivan() {
		return aktivan;
	}


	public void setAktivan(boolean aktivan) {
		this.aktivan = aktivan;
	}


	@Override
	public String toString() {
		return "Vozilo [marka=" + marka + ", model=" + model + ", tip=" + tip + ", registracija=" + registracija
				+ ", godinaProizvodnje=" + godinaProizvodnje + ", uUpotrebi=" + uUpotrebi + ", napomena=" + napomena
				+ ", aktivan=" + aktivan + "]";
	}
	
	
	
	
	

}
