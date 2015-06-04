package org.getalp.lexsema.io.SemecoreDefinitionExpender;

import java.util.ArrayList;
import java.util.List;

import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;

public class SemecoreDefinitionExpender {
	
	private ArrayList<ArrayList <Word>> dico;
	private ArrayList <Integer> occurence;
	
	//Supprimer le lrloader
	public SemecoreDefinitionExpender (TextLoader semCor, int occurenceMax){
		Word courant;
		int i, j;
		dico = new ArrayList<ArrayList <Word>>();
		occurence = new ArrayList<Integer>();
		
		for (Text t : semCor) {
			for (Sentence s : t.sentences()){
				for(i=0; i<s.size();i++){
					courant=s.getWord(0, i);
					if(courant.getSemanticTag()!=null){
					
						//On parcours le dico pour verifier que le mot courant n'a pas deja été ajouté
						int offset=appartient(courant);
						//s'il est dans la liste, on ajoute les mots de la phrase à sa liste courant
						if(offset!=-1){
							occurence.set(offset, occurence.get(offset)+1);
							for(int k=0; k<s.size();k++){
								//Tant que le mot est different du mot courant et de null, on l'ajoute							}
								if(s.getWord(0, k).getLemma()!= null && s.getWord(0, k).getSemanticTag()!=null && (!courant.getLemma().equals(s.getWord(0, k).getLemma()) || !courant.getSemanticTag().equals(s.getWord(0, k).getSemanticTag()))){
									dico.get(offset).add(s.getWord(0, k));
								}
							}
						}
						//sinon, on ajoute le mot en tete de liste d'une nouvelle case, puis on ajoute les mots de son contexte à la suite
						else{
							occurence.add(1);
							dico.add(new ArrayList<Word>());
							dico.get(dico.size()-1).add(courant);
							for(int k=0; k<s.size();k++){
								//Tant que le mot est different du mot courant, on l'ajoute							}
								if(s.getWord(0, k).getLemma()!= null && s.getWord(0, k).getSemanticTag()!=null && (!courant.getLemma().equals(s.getWord(0, k).getLemma()) || !courant.getSemanticTag().equals(s.getWord(0, k).getSemanticTag()))){
									dico.get(dico.size()-1).add(s.getWord(0, k));
								}
							}
						}
					}
				}
			}
		}
		
		//Supression des mots avec une occurence élevée car non pertinents
		i=0;
		while(i<occurence.size()){
			if(occurence.get(i)>occurenceMax){
				occurence.remove(i);
				dico.remove(i);
			}else{
				i++;
			}
		}
	}
	
	//retourne -1 si le mot n'est pas dans la liste, sinon retourne la position
	public int appartient(Word w){
		return appartient(w.getLemma(), w.getSemanticTag());
	}
	
	public int appartient(String lemma, String sens){
		boolean estDansListe=false;
		int i =0;		
		while(dico != null && i<dico.size() && !estDansListe){
			if(dico.get(i).get(0).getLemma().equals(lemma) && dico.get(i).get(0).getSemanticTag().equals(sens)){
				estDansListe=true;
			}else{
				i++;
			}
		}
		if (estDansListe){
			return i;
		}else{
			return -1;
		}
	}
	
	public boolean estDansListe(Word w){
		return estDansListe(w.getLemma());
	}
	
	public boolean estDansListe(String lemma){
		boolean estDansListe=false;
		int i=0;
		while(i<dico.size() && !estDansListe){
			if(dico.get(i).get(0).getLemma().equals(lemma)){
				estDansListe=true;
			}
			i++;
		}
		return estDansListe;
	}
	
	//retourne null, si le mot n'est pas dans la liste, sinon retourne la liste (pour chaque sens) de mot associé au contexte
	public ArrayList<ArrayList<Word>> motsDuContexte(Word w){
		return motsDuContexte(w.getLemma());
	}
	
	public ArrayList<ArrayList<Word>> motsDuContexte(String lemma){
		ArrayList<ArrayList<Word>> resultat=new ArrayList<ArrayList<Word>>();
		if(estDansListe(lemma)){
			for (int i = 0; i<dico.size(); i++){
				if(dico.get(i).get(0).getLemma().equals(lemma)){
					resultat.add(new ArrayList<Word>());
					for (int j=0; j<dico.get(i).size();j++){
						resultat.get(resultat.size()-1).add(dico.get(i).get(j));
					}
				}
			}
		}
		return resultat;
	}

	public ArrayList<ArrayList<Word>> getDico() {
		return dico;
	}

	public void setDico(ArrayList<ArrayList<Word>> dico) {
		this.dico = dico;
	}
}