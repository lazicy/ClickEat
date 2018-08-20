package beans;

import java.util.ArrayList;

public class Kupac extends User {
	
	private int bodovi;
	
	private ArrayList<Porudzbina> porudzbine = new ArrayList<>();
	private ArrayList<Restoran> favRestorani = new ArrayList<>();
	
	
	public Kupac(String username, String password, String firstname, String lastname, int role, String phone, String email, String registrationDate,
			ArrayList<Porudzbina> porudzbine, ArrayList<Restoran> favRestorani, int bodovi, boolean aktivan) {
		super(username, password, firstname, lastname, role, phone, email, registrationDate, aktivan);
		this.porudzbine = porudzbine;
		this.favRestorani = favRestorani;
		this.bodovi = bodovi;
	}
	
	public Kupac(String username, String password, String firstname, String lastname, int role, String phone, String email, String registrationDate) {
		super(username, password, firstname, lastname, role, phone, email, registrationDate, true);
		this.bodovi = 0;
	}

	public ArrayList<Porudzbina> getPorudzbine() {
		return porudzbine;
	}

	public void setPorudzbine(ArrayList<Porudzbina> porudzbine) {
		this.porudzbine = porudzbine;
	}

	public ArrayList<Restoran> getFavRestorani() {
		return favRestorani;
	}

	public void setFavRestorani(ArrayList<Restoran> favRestorani) {
		this.favRestorani = favRestorani;
	}
	
	
	

	public int getBodovi() {
		return bodovi;
	}

	public void setBodovi(int bodovi) {
		this.bodovi = bodovi;
	}


	@Override
	public String toString() {
		return "Kupac [porudzbine=" + porudzbine + ", favRestorani=" + favRestorani + ", getUsername()=" + getUsername()
				+ "]";
	}
	
	
	
	
	
	

	
}
