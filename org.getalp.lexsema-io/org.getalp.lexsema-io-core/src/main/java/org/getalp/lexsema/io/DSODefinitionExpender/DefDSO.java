package org.getalp.lexsema.io.DSODefinitionExpender;

import java.util.ArrayList;
	
public class DefDSO {
	
	private String lemma;
	private String definition;
	private ArrayList<String> contexte;
	private int occurence;
	
	//private sens (pas obligatoire pour l'instant car place dans la liste equivalent)
	//private partOfSpeech (pas obligatoire car 2 listes : une pour n et une pour v)
	public DefDSO(){
		occurence=0;
	}

	public DefDSO(String lemma, String definition){
		this.lemma=lemma;
		this.definition=definition;
		occurence=0;
		contexte=new ArrayList<String>();
	}
	
	public void addContexte(String contexte){
		//System.out.println(contexte);
		int i=0, k;
		while(i<contexte.length()){
			if(contexte.charAt(i)>=97 && contexte.charAt(i)<=122){
				k=i+1;
				while((k)<contexte.length() && contexte.charAt(k)>=97 && contexte.charAt(k)<=122){
					k++;
				}
				this.contexte.add(contexte.substring(i, k));
				//System.out.println(contexte.substring(i, k));
				i=k;
			}else{
				i++;
			}
		}
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public ArrayList<String> getContexte() {
		return contexte;
	}

	public void setContexte(String contexte) {
		this.contexte=new ArrayList<String>();
		addContexte(contexte);
	}
	
	public int getOccurence() {
		return occurence;
	}

	public void setOccurence(int occurence) {
		this.occurence = occurence;
	}
}