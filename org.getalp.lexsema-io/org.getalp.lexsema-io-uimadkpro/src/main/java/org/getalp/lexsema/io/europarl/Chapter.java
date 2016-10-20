package org.getalp.lexsema.io.europarl;

import java.util.List;

public class Chapter {
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Speaker> getSpk() {
		return spk;
	}
	public void setSpk(List<Speaker> spk) {
		this.spk = spk;
	}
	private List<Speaker> spk; 
}
