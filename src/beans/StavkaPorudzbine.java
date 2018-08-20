package beans;

public class StavkaPorudzbine {
	
	private Artikal artikal;
	private int brojPorcija;
	
	public StavkaPorudzbine(Artikal artikal, int brojPorcija) {
		this.artikal = artikal;
		this.brojPorcija = brojPorcija;
	}

	public Artikal getArtikal() {
		return artikal;
	}

	public void setArtikal(Artikal artikal) {
		this.artikal = artikal;
	}

	public int getBrojPorcija() {
		return brojPorcija;
	}

	public void setBrojPorcija(int brojPorcija) {
		this.brojPorcija = brojPorcija;
	}
	
	public void refreshPorcije(int brojPorcija) {
		this.brojPorcija += brojPorcija;
	}
	
	public int getTipArtikla() {
		if (this.artikal.getTip() == 1) {
			return 1;
		} else if (this.artikal.getTip() == 2) {
			return 2;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return "StavkaPorudzbine [artikal=" + artikal + ", brojPorcija=" + brojPorcija + "]";
	}
	
	

}
