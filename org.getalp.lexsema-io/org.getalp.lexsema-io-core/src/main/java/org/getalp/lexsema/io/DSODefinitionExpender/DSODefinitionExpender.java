package org.getalp.lexsema.io.DSODefinitionExpender;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DSODefinitionExpender {
	ArrayList<ArrayList<DefDSO>> dico;

	public DSODefinitionExpender(int occurence){
		dico = new ArrayList<ArrayList<DefDSO>>();
		String fichier="";
		String contexte="";
		InputStream ips;
		InputStreamReader ipsr;
		BufferedReader br;
		String ligne;
		char courant;
		int i, l=1, taille=0, d=0;
		for(int a=0; a<2; a++){
			if(a==0){
				fichier ="../data/newSemcor/vdefs.txt";
				contexte="../data/newSemcor/v.txt";
			}else{
				fichier ="../data/newSemcor/ndefs.txt";
				contexte="../data/newSemcor/n.txt";
				taille=dico.size();
			}

			//Generation de la liste de mots/sense et de leurs definition
			if(!fichier.equals(null)){
				try{
					ips=new FileInputStream(fichier); 
					ipsr=new InputStreamReader(ips);
					br=new BufferedReader(ipsr);
					String nouveauMot = "Synonyms/Hypernyms (Ordered by Frequency) of ";
					String nouveauSense = "Sense ";
					String lemmaCourant = "";
					String definition="";
					boolean nextIsSense = false, def;
					int pos=0;
					//Parseur
					while ((ligne=br.readLine())!=null){
						//Si c'est un nouveau mot : 
						if(ligne.length()>44 && nouveauMot.equals(ligne.substring(0, 45))){
							lemmaCourant = ligne.substring(50, ligne.length());
							if(this.estDansDico(lemmaCourant)){
								pos=this.getPos(lemmaCourant);
							}else{
								pos=dico.size();
								d=0;
								dico.add(new ArrayList<DefDSO>());
							}
						}
						//Si c'est un nouveau sense, la prochaine ligne devra etre traité, d'ou le boolean
						else if(ligne.length()>5 && nouveauSense.equals(ligne.substring(0, 6))){
							dico.get(pos).add(new DefDSO());
							nextIsSense = true;
						}
						//Ligne a traitée de nouveau sens
						else if(nextIsSense){
							//On lui affecte son lemma
							dico.get(pos).get(dico.get(pos).size()-1).setLemma(lemmaCourant);
							//On cherche la definition et on lui affecte
							i=0;
							def = false;
							while(i<ligne.length()){
								courant = ligne.charAt(i);
								//Si c'est le debut de la definition
								if(courant=='('){
									def = true;
								}
								//Si on est dans la definition, on remplit la string intermediaire
								else if(def){
									//Si c'est pas la fin de la definition
									if((courant >= 97 && courant <= 122) || courant==32){
										definition = definition+courant;
									}else{
										def = false;
										i=ligne.length();
									}
								}
								i++;
							}
							dico.get(pos).get(dico.get(pos).size()-1).setDefinition(definition);
							definition = new String();
							nextIsSense = false;
						}
					}
					br.close(); 
				}		
				catch (Exception e){
					System.out.println(e.toString());
				}
			}
			
			//horrible
			if(a==0){
				dico.add(49, dico.get(50));
				dico.remove(51);
				/*for(int b=0; b<dico.size();b++){
					System.out.println(b+" "+dico.get(b).get(0).getLemma());
				}*/
			}

			//Recuperation des contexte
			if(!contexte.equals(null)){
				try{
					ips=new FileInputStream(contexte); 
					ipsr=new InputStreamReader(ips);
					br=new BufferedReader(ipsr);
					String phrase="", ligneValide;
					String lemma="";
					char courantValide;
					int sense=0, numeroMot=-1+taille, c=0, nbLigne=0, k;
					boolean debutContexte, ID, isID, isFinID, ajoutEspace, nouveauMot=false, correspondance=false;;

					InputStream ipsValide;
					InputStreamReader ipsrValide;
					BufferedReader brValide;
					boolean isIDValide=false, IDValide=false, nouveauMotValide=true, arret=false;
					String lemmaValide="";

					//Lecture ligne par ligne
					while ((ligne=br.readLine())!=null){
						//Verification de la correspondance mot du dico / mot du contexte
						//System.out.println(ligne);
						nbLigne++;
						i=0;
						debutContexte=false;
						ID=false;
						ajoutEspace=false;
						isID=false;
						isFinID=false;

						//Si c'est un nouveau mot : debut de ligne ca
						if(ligne.charAt(0)=='c' && ligne.charAt(1)=='a'){
							if(!nouveauMot){
								correspondance=false;
								nouveauMot=true;
								nouveauMotValide=true;
								arret=false;
								//c=0;
								d++;
								//System.out.println(dico.get(numeroMot+1).get(0).getLemma()+" : "+ligne);
								ipsValide=new FileInputStream(contexte); 
								ipsrValide=new InputStreamReader(ipsValide);
								brValide=new BufferedReader(ipsrValide);
								for(int j=0; j<nbLigne-1;j++){
									ligneValide=brValide.readLine();
								}
								while ((ligneValide=brValide.readLine())!=null && !correspondance && !arret){
									//System.out.println(ligneValide);
									if(ligneValide.charAt(0)=='c' && ligneValide.charAt(1)=='a'){
										if(!nouveauMotValide){
											arret=true;
										}
									}else{
										nouveauMotValide=false;
									}
									k=0;
									while(k<ligneValide.length()){
										courantValide = ligneValide.charAt(k);
										if(courantValide==62){
											if(isIDValide){
												IDValide=true;
												isIDValide=false;
											}else{
												isIDValide=true;
											}
										}else if(courantValide==60 && IDValide){
											IDValide=false;
										}else if(IDValide){
											if(courantValide >= 97 && courantValide <= 122){
												lemmaValide=lemmaValide+courantValide;
											}
										}
										k++;
									}
									//System.out.println("jambon1");
									if(lemmaValide.equals(dico.get(numeroMot+1).get(0).getLemma())){
										correspondance = true;
										numeroMot++;
									}
									//System.out.println("jambon2");
									lemmaValide="";
									ligneValide=brValide.readLine();
								}
							}
						}else{
							if(nouveauMot){
								nouveauMot=false;
							}
						}

						if(correspondance){
							//Lecture caractere par caractere
							while(i<ligne.length()){
								courant = ligne.charAt(i);

								//Si le caractere courant est un # alors debut du contexte
								if(!debutContexte){
									if(courant==35){
										debutContexte=true;
									}

									//Si on est dans le contexte
								}else{

									//Si le caractere courant est une majuscule : on convertit en minuscule
									if(courant >= 65 && courant <= 90){
										courant = (char) (courant+32);
									}

									//Si le caractere courant est une apostrophe : on le remplace par un espace
									else if(courant == 39){
										courant = (char)(32);
									}

									//Si le caractere courant est une lettre	
									if(courant >= 97 && courant <= 122){

										//On autorise l'ajout d'espace
										ajoutEspace = true;

										//On l'ajoute au contexte
										phrase=phrase+courant;
										if(ID){

											//Si le caractere courant fais partie du mot contexté, on l'ajoute egalement au lemma
											if(courant >= 97 && courant <= 122){
												lemma=lemma+courant;
											}
										}

										//Si le caractere courant est un espace
									}else if(courant == 32){
										if(ajoutEspace){
											phrase=phrase+courant;
											ajoutEspace=false;
										}

										//Si le caractere courant est le numero de sense du mot contexté on le reléve
									}else if(courant >= 48 && courant <= 57 && ID){
										sense=10*sense+((int)courant)-48;
										/*if(sense==5){
											System.out.println(sense);
										}*/

										//Si on entre dans les borne du mot et de son sense : caractere courant = >
									}else if(courant==62 && !ID){
										if(!isID){
											isID=true;
										}else{
											ID=true;
											isID=false;
										}

										//Si on sort des borne du mot et de son sense : caractere courant = <
									}else if(courant==60 && ID){
										if(!isFinID){
											isFinID=true;
										}else{
											ID=false;
											isFinID=false;
										}

									}else if(isID){
										isID=false;

									}else if(isFinID){
										isFinID=false;
									}
								}	
								i++;
							}
							if(sense>0 && sense<=dico.get(numeroMot).size() && dico.get(numeroMot).get(sense-1).getOccurence()<occurence){
								boolean trouver = false;
								int p=0;
								while(p<numeroMot && !trouver){
									if(dico.get(p).get(0).getLemma().equals(lemma)){
										trouver=true;
										numeroMot--;
									}else{
										p++;
									}
								}
								if(dico.get(p).get(sense-1).getContexte()!=null){
									phrase = phrase+dico.get(p).get(sense-1).getContexte();
								}
								dico.get(p).get(sense-1).setContexte(phrase);
								dico.get(p).get(sense-1).setOccurence(dico.get(p).get(sense-1).getOccurence()+1);
							}
						}
						phrase="";
						lemma="";
						sense=0;
						ligne=br.readLine();
						nbLigne++;
					}
					br.close();
				}
				catch (Exception e){
					System.out.println(e.toString());
				}
			}
		}
		for (int m=0; m<dico.size(); m++){
			System.out.println(dico.get(m).get(0).getLemma());
			for (int n=0; n<dico.get(m).size(); n++){
				System.out.println(n+" : "+dico.get(m).get(n).getContexte().size()+" "+dico.get(m).get(n).getDefinition());
			}
			System.out.println("");
		}
	}

	/*public DSODefinitionExpender(char partOfSpeech, int occurence){
		dico = new ArrayList<ArrayList<DefDSO>>();
		String fichier="";
		String contexte="";
		InputStream ips;
		InputStreamReader ipsr;
		BufferedReader br;
		String ligne;
		char courant;
		int i, l=1;

		if(partOfSpeech=='v'){
			fichier ="../data/newSemcor/vdefs.txt";
			contexte="../data/newSemcor/v.txt";
		}else if(partOfSpeech=='n'){
			fichier ="../data/newSemcor/ndefs.txt";
			contexte="../data/newSemcor/n.txt";
		}else{
			System.out.println("erreur");
		}

		//Generation de la liste de mots/sense et de leurs definition
		if(!fichier.equals(null)){
			try{
				ips=new FileInputStream(fichier); 
				ipsr=new InputStreamReader(ips);
				br=new BufferedReader(ipsr);
				String nouveauMot = "Synonyms/Hypernyms (Ordered by Frequency) of ";
				String nouveauSense = "Sense ";
				String lemmaCourant = "";
				String definition="";
				boolean nextIsSense = false, def;
				//Parseur
				while ((ligne=br.readLine())!=null){
					//Si c'est un nouveau mot : 
					if(ligne.length()>44 && nouveauMot.equals(ligne.substring(0, 45))){
						dico.add(new ArrayList<DefDSO>());
						lemmaCourant = ligne.substring(50, ligne.length());
					}
					//Si c'est un nouveau sense, la prochaine ligne devra etre traité, d'ou le boolean
					else if(ligne.length()>5 && nouveauSense.equals(ligne.substring(0, 6))){
						dico.get(dico.size()-1).add(new DefDSO());
						nextIsSense = true;
					}
					//Ligne a traitée de nouveau sens
					else if(nextIsSense){
						//On lui affecte son lemma
						dico.get(dico.size()-1).get(dico.get(dico.size()-1).size()-1).setLemma(lemmaCourant);
						//On cherche la definition et on lui affecte
						i=0;
						def = false;
						while(i<ligne.length()){
							courant = ligne.charAt(i);
							//Si c'est le debut de la definition
							if(courant=='('){
								def = true;
							}
							//Si on est dans la definition, on remplit la string intermediaire
							else if(def){
								//Si c'est pas la fin de la definition
								if((courant >= 97 && courant <= 122) || courant==32){
									definition = definition+courant;
								}else{
									def = false;
									i=ligne.length();
								}
							}
							i++;
						}
						dico.get(dico.size()-1).get(dico.get(dico.size()-1).size()-1).setDefinition(definition);
						definition = new String();
						nextIsSense = false;
					}
				}
				br.close(); 
			}		
			catch (Exception e){
				System.out.println(e.toString());
			}
		}

		//Recuperation des contexte
		if(!contexte.equals(null)){
			try{
				ips=new FileInputStream(contexte); 
				ipsr=new InputStreamReader(ips);
				br=new BufferedReader(ipsr);
				String phrase="";
				String lemma="";
				int sense=0, numeroMot=-1, c=0;
				boolean debutContexte, ID, isID, isFinID, ajoutEspace, nouveauMot=false;

				//Lecture ligne par ligne
				while ((ligne=br.readLine())!=null){
					i=0;
					debutContexte=false;
					ID=false;
					ajoutEspace=false;
					isID=false;
					isFinID=false;

					//Si c'est un nouveau mot : debut de ligne ca
					if(ligne.charAt(0)=='c' && ligne.charAt(1)=='a'){
						if(!nouveauMot){
							//c=0;
							numeroMot++;
							nouveauMot=true;
						}
					}else{
						if(nouveauMot){
							nouveauMot=false;
						}
					}

					//Lecture caractere par caractere
					while(i<ligne.length()){
						courant = ligne.charAt(i);

						//Si le caractere courant est un # alors debut du contexte
						if(!debutContexte){
							if(courant==35){
								debutContexte=true;
							}

						//Si on est dans le contexte
						}else{

							//Si le caractere courant est une majuscule : on convertit en minuscule
							if(courant >= 65 && courant <= 90){
								courant = (char) (courant+32);
							}

							//Si le caractere courant est une apostrophe : on le remplace par un espace
							else if(courant == 39){
								courant = (char)(32);
							}

							//Si le caractere courant est une lettre	
							if(courant >= 97 && courant <= 122){

								//On autorise l'ajout d'espace
								ajoutEspace = true;

								//On l'ajoute au contexte
								phrase=phrase+courant;
								if(ID){

									//Si le caractere courant fais partie du mot contexté, on l'ajoute egalement au lemma
									if(courant >= 97 && courant <= 122){
										lemma=lemma+courant;
									}
								}

							//Si le caractere courant est un espace
							}else if(courant == 32){
								if(ajoutEspace){
									phrase=phrase+courant;
									ajoutEspace=false;
								}

							//Si le caractere courant est le numero de sense du mot contexté on le reléve
							}else if(courant >= 48 && courant <= 57 && ID){
								sense=10*sense+((int)courant)-48;

							//Si on entre dans les borne du mot et de son sense : caractere courant = >
							}else if(courant==62 && !ID){
								if(!isID){
									isID=true;
								}else{
									ID=true;
									isID=false;
								}

							//Si on sort des borne du mot et de son sense : caractere courant = <
							}else if(courant==60 && ID){
								if(!isFinID){
									isFinID=true;
								}else{
									ID=false;
									isFinID=false;
								}

							}else if(isID){
								isID=false;

							}else if(isFinID){
								isFinID=false;
							}
						}	
						i++;
					}
					if(sense>0 && sense<dico.get(numeroMot).size() && dico.get(numeroMot).get(sense-1).getOccurence()<occurence){
						if(dico.get(numeroMot).get(sense-1).getContexte()!=null){
							phrase = phrase+dico.get(numeroMot).get(sense-1).getContexte();
						}
						dico.get(numeroMot).get(sense-1).setContexte(phrase);
						dico.get(numeroMot).get(sense-1).setOccurence(dico.get(numeroMot).get(sense-1).getOccurence()+1);
					}
					phrase="";
					lemma="";
					sense=0;
					ligne=br.readLine();
				}
				br.close();
			}
			catch (Exception e){
				System.out.println(e.toString());
			}
		}
	}*/

	public boolean estDansDico(String lemma){
		boolean resultat = false;
		int i=0;
		while(i<dico.size() && !resultat){
			if(dico.get(i).get(0).getLemma().equals(lemma)){
				resultat = true;
			}
			i++;
		}
		return resultat;
	}

	public String getDefinition(String lemma, int sense){
		String resultat = "";
		int i=0;
		while(i<dico.size() && !dico.get(i).get(0).equals(lemma)){
			i++;
		}
		if(dico.get(i).get(0).equals(lemma) && sense<dico.get(i).size() && dico.get(i).get(sense).getDefinition()!=null){
			resultat = dico.get(i).get(sense).getDefinition();
		}
		return resultat;
	}

	public ArrayList<String> getContexte(String lemma, int sense){
		ArrayList<String> resultat = new ArrayList<String>();
		int i=0;
		while(i<dico.size() && !dico.get(i).get(0).equals(lemma)){
			i++;
		}
		if(dico.get(i).get(0).equals(lemma) && sense<dico.get(i).size() && dico.get(i).get(sense).getContexte()!=null){
			resultat = dico.get(i).get(sense).getContexte();
		}
		return resultat;
	}

	public String getDefinition(int lemma, int sense){
		String resultat = "";
		if(lemma<dico.size()){
			if(sense<dico.get(lemma).size()){
				resultat=dico.get(lemma).get(sense).getDefinition();
			}
		}
		return resultat;
	}

	public List<String> getContexte(int lemma, int sense){
		List<String> resultat = new ArrayList<String>();
		if(lemma<dico.size()){
			if(sense<dico.get(lemma).size()){
				resultat=dico.get(lemma).get(sense).getContexte();
			}
		}
		return resultat;
	}

	public int size(){
		return dico.size();
	}

	public int sizeSense(int i){
		int resultat =0;
		if(i<dico.size()){
			resultat=dico.get(i).size();
		}
		return resultat;
	}

	public String getLemma(int i){
		String resultat ="";
		if(i<dico.size()){
			resultat=dico.get(i).get(0).getLemma();
		}
		return resultat;
	}

	public int getPos(String lemma){
		int resultat =-1;
		if(estDansDico(lemma)){
			resultat++;
			while(resultat<dico.size() && !dico.get(resultat).get(0).getLemma().equals(lemma)){
				resultat++;
			}
		}
		return resultat;
	}

	public ArrayList<DefDSO> getDefDSO(String lemma){
		ArrayList<DefDSO> resultat = null;
		int i=0;
		if(estDansDico(lemma)){
			resultat=new ArrayList<DefDSO>();
			while(i<dico.size() && !dico.get(i).get(0).getLemma().equals(lemma)){
				i++;
			}
			resultat=dico.get(i);
		}
		return resultat;
	}
}