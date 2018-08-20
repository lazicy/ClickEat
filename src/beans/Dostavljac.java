package beans;

import java.util.ArrayList;

public class Dostavljac extends User {
	
	private ArrayList<Porudzbina> porudzbine = new ArrayList<>();
	private Vozilo vozilo;
	

	public Dostavljac(String username, String password, String firstname, String lastname, int role, String phone, String email, String registrationDate,
			ArrayList<Porudzbina> porudzbine, Vozilo vozilo, boolean aktivan) {
		super(username, password, firstname, lastname, role, phone, email, registrationDate, aktivan);
		this.vozilo = vozilo;
		this.porudzbine = porudzbine;
	}
	
	public Dostavljac(String username, String password, String firstname, String lastname, int role, String phone, String email, String registrationDate) {
		super(username, password, firstname, lastname, role, phone, email, registrationDate, true);
		this.vozilo = null;
	}


	public Vozilo getVozilo() {
		return vozilo;
	}


	public void setVozilo(Vozilo vozilo) {
		this.vozilo = vozilo;
	}


	public ArrayList<Porudzbina> getPorudzbine() {
		return porudzbine;
	}


	public void setPorudzbine(ArrayList<Porudzbina> porudzbine) {
		this.porudzbine = porudzbine;
	}


	@Override
	public String toString() {
		return "Dostavljac [vozilo=" + vozilo + ", porudzbine=" + porudzbine + ", getUsername()=" + getUsername() + "]";
	}



	

}
