package reseau;

import java.io.Serializable;

public class Tram implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	byte[] tabOct ; 
	int id ; 
	
	
	public Tram(byte[] t ,int i ) {
		this.tabOct=t ; 
		this.id = i ; 
	}


	public byte[] getTabOct() {
		return tabOct;
	}


	public void setTabOct(byte[] tabOct) {
		this.tabOct = tabOct;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	
	
}
