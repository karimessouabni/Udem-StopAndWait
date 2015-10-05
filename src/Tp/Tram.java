package Tp;

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
	public Tram(Tram t) {
		this.tabOct=t.getTabOct() ; 
		this.id = t.getId() ; 
	}
	
	public Tram() {
		// TODO Auto-generated constructor stub
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

	public void copie (Tram t)
	{
		this.id = t.getId();
		this.tabOct = t.getTabOct();
	}
	
}
