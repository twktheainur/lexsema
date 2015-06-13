package org.getalp.lexsema.wsd.experiments;

import java.util.List;

public class Speaker {
	String id ;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Paragraph> getPar() {
		return par;
	}
	public void setPar(List<Paragraph> par) {
		this.par = par;
	}
	public String getLanguage() {
		return Language;
	}
	public void setLanguage(String language) {
		Language = language;
	}
	List <Paragraph> par;
	String Language;
}
