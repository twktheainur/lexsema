package org.getalp.lexsema.io.resource.wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.*;

import org.getalp.lexsema.io.DSODefinitionExpender.DSODefinitionExpender;
import org.getalp.lexsema.io.SemecoreDefinitionExpender.SemecoreDefinitionExpender;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.SenseImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.cache.SenseCacheImpl;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.getalp.lexsema.util.StopList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class WordnetLoader implements LRLoader {
    private static Logger logger = LoggerFactory.getLogger(WordnetLoader.class);
    private final Dictionary dictionary;
    private boolean hasExtendedSignature;
    private boolean shuffle;
    private boolean usesStopWords;
    private boolean stemming;

    private boolean loadDefinitions;
    private boolean loadRelated;


    public WordnetLoader(String path) {

        URL url = null;
        try {
            url = new URL("file", null, path);
        } catch (MalformedURLException e) {
            logger.info(e.getLocalizedMessage());
        }
        if (url != null) {
            dictionary = new Dictionary(url);
            try {
                dictionary.open();
            } catch (IOException e) {
                logger.info(e.getLocalizedMessage());
            }
        } else {
            dictionary = null;
        }
    }

    private List<Sense> getSenses(String lemma, String pos) {
        List<Sense> senses = new ArrayList<>();
        IIndexWord iw = getWord(lemma + "%" + pos);
        if (iw != null) {
            for (int j = 0; j < iw.getWordIDs().size(); j++) {

                StringSemanticSignature signature = new StringSemanticSignatureImpl();
                IWord word = dictionary.getWord(iw.getWordIDs().get(j));
                if (loadDefinitions) {
                    String def = word.getSynset().getGloss();
                    addToSignature(signature, def);
                }

                Sense s = new SenseImpl(word.getSenseKey().toString());

                if (loadRelated) {
                    Map<IPointer, List<IWordID>> rm = word.getRelatedMap();
                    for (IPointer p : rm.keySet()) {
                        for (IWordID iwd : rm.get(p)) {
                            StringSemanticSignature localSignature = new StringSemanticSignatureImpl();
                            addToSignature(localSignature, dictionary.getWord(iwd).getSynset().getGloss());
                            if (hasExtendedSignature) {
                                signature.appendSignature(localSignature);
                            }
                            s.addRelatedSignature(p.getSymbol(), localSignature);
                        }
                    }
                }
                //if (hasExtendedSignature) {
                s.setSemanticSignature(signature);
                //}
                senses.add(s);
            }
        }
        return senses;
    }

    @Override
    public List<Sense> getSenses(Word w) {
        List<Sense> senses;
        senses = SenseCacheImpl.getInstance().getSenses(w);
        if (senses == null) {
            if (w != null) {
                if (w.getPartOfSpeech() == null || w.getPartOfSpeech().isEmpty()) {
                    senses = getSenses(w.getLemma(), "n");
                    senses.addAll(getSenses(w.getLemma(), "r"));
                    senses.addAll(getSenses(w.getLemma(), "a"));
                    senses.addAll(getSenses(w.getLemma(), "v"));
                } else {
                    senses = getSenses(w.getLemma(), w.getPartOfSpeech());
                }
            }
            if (shuffle) {
                Collections.shuffle(senses);
            }
            SenseCacheImpl.getInstance().addToCache(w, senses);
        }

        return senses;
    }
    
    public List<Sense> getSensesEnrichisSemecore(Word w, List<Sense> senses, SemecoreDefinitionExpender definitionExpender, int profondeur){
    	//dictionnaire retourne une liste de liste de mot du contexte, pour chaque sens mot a traiter (sensesContexte)
		
    			ArrayList <ArrayList <Word>> sensesContexte = definitionExpender.motsDuContexte(w);
    			//System.out.println(w.getLemma());
    			
    			//parcours la liste de senses
    			int j=0;
    			boolean estDansDico;
    			String sens;
    			ArrayList <Word> senseContexte;
    			//On parcours la liste de sens que l'on doit enrichir
    			while(j<senses.size()){        	
    				sens=senses.get(j).getId();
    				senseContexte=findSenseContexte(sensesContexte, sens);
    				//i : place du sens courant dans la liste de mots du contexte
    				if(senseContexte!=null){
    					//On recupere les sens des mots du contexte du sens à enrichir
    					//On retrouve le sens qui nous interesse
    					//et on l'ajoute au sense courant à enrichir
    					int k=1;
    					while(k<senseContexte.size()){
    						Word motCourantContexte=senseContexte.get(k);
    						//Récupére le sens du mot du contexte pour avoir acces à sa definition
    						Sense senseAjouter;
    						List<Sense> sensesCourant=getSenses(motCourantContexte);
    						if(profondeur!=0){
    							sensesCourant=getSensesEnrichisSemecore(w, sensesCourant, definitionExpender, profondeur-1);
    						}
    						senseAjouter=findSense(motCourantContexte.getLemma()+"%"+motCourantContexte.getSemanticTag(), sensesCourant);
    						if(senseAjouter!=null){
    							ajouterSense(senses.get(j), senseAjouter);
    						}
    						k++;
    					}
    				}
    				j++;
    			}
    			return senses;
    }
    
    public List<Sense> getSensesEnrichisDSO(Word w, List<Sense> senses, DSODefinitionExpender contexteDSO){
    	String defCourante;
    	if(contexteDSO.estDansDico(w.getLemma())){
			int scoreMax, scoreCourant, sens=0, lemma=contexteDSO.getPos(w.getLemma());
			for (int i=0; i<senses.size(); i++){
				scoreMax=0;
				defCourante=senses.get(i).getSemanticSignature().toString();
				/*System.out.println(w.getLemma()+" "+contexteDSO.getLemma(lemma));
				System.out.println(defCourante);*/
				for(int j =0; j<contexteDSO.sizeSense(lemma); j++){
					scoreCourant=score(defCourante, contexteDSO.getDefinition(lemma, j));
					if(scoreCourant>scoreMax){
						scoreMax=scoreCourant;
						sens=j;
						//System.out.println("jambon");
					}
				}
				/*System.out.println(w.getLemma());
				System.out.println("");
				StringSemanticSignature semanticTest=(StringSemanticSignature)senses.get(i).getSemanticSignature();
				for(int k=0; k< semanticTest.getSymbols().size(); k++){
					System.out.println(semanticTest.getSymbol(k).getSymbol());
				}
				System.out.println("");*/
				List<String> contexte=contexteDSO.getContexte(lemma, sens);
				if(contexte!=null){
				/*	for(int k=0; k< contexte.size(); k++){
						System.out.println(contexte.get(k));
					}
					System.out.println(senses.get(i).getSemanticSignature().toString());*/
						StringSemanticSignature semantic = (StringSemanticSignature)senses.get(i).getSemanticSignature();
						semantic.addSymbolString(contexteDSO.getContexte(lemma, sens));
						senses.get(i).setSemanticSignature(semantic);
						//for(int k=0; k< senses.get(i).getSemanticSignature().size(); k++){
							//System.out.println(senses.get(i).getSemanticSignature().toString());
						//}
						//senses.get(i).getSemanticSignature().addSymbol(contexteDSO.getContexte(lemma, sens).get(k));
					//}
				}
				/*semanticTest=(StringSemanticSignature)senses.get(i).getSemanticSignature();
				for(int k=0; k< semanticTest.getSymbols().size(); k++){
					System.out.println(semanticTest.getSymbol(k).getSymbol());
				}
				System.out.println("");
				System.out.println("");
				System.out.println("");
				System.out.println(verbeDSO.getDefinition(lemma, sens));
				System.out.println(senses.get(i).getSemanticSignature().toString());
				System.out.println("");*/
			}
		}
    	return senses;
    }
    
  //retourne le sense, parmis la liste de sens, correspondant au mot du contexte enrichissant
  	//null s'il n'est pas dans 
  	public Sense findSense(String id, List<Sense> liste){
  		Sense resultat=null;
  		for (int l=0; l<liste.size(); l++){
  			if(id.equals(liste.get(l).getId())){
  				resultat=liste.get(l);
  			}
  		}
  		return resultat;
  	}
  	
  	//retourne la liste de mot du contexte correspondant au sens à traiter, 
  	//null si le sens a traiter n'est pas dans le dictionnaire
  	public ArrayList <Word> findSenseContexte(ArrayList <ArrayList <Word>> sensesContexte, String sens){
  		ArrayList <Word> resultat=null;
  		int i=0;
  		String idSensesContexteCourant;
  		//Pour chaque sens à enrichir, on parcours la liste de liste renvoyer par le dico
  		//correspondant aux mots (et leur sens) du contexte pour chaque sens à enrichir
  		while(i<sensesContexte.size() && resultat==null){
  			//pour chaque sens, verifier s'il est dans le senseContexte (senses.get(j).getId())==senseContexte(i).getId))
  			idSensesContexteCourant=sensesContexte.get(i).get(0).getLemma()+"%"+sensesContexte.get(i).get(0).getSemanticTag();
  			if(sens.equals(idSensesContexteCourant)){
  				resultat=sensesContexte.get(i);
  			}else{
  				i++;
  			}
  		}
  		return resultat;
  	}
  	
  	//Ajoute le sense du mot du contexte courant à la definition du sens du mot à traiter
  	public void ajouterSense(Sense sense, Sense senseAjouter){
  		StringSemanticSignature semantic = new StringSemanticSignatureImpl();
  		StringSemanticSignature signature = (StringSemanticSignature)sense.getSemanticSignature();
  		StringSemanticSignature signatureAjouter = (StringSemanticSignature)senseAjouter.getSemanticSignature();
  		semantic.addSymbolString(signature.getSymbols());
  		semantic.addSymbolString(signatureAjouter.getSymbols());
  		sense.setSemanticSignature(semantic);
  	}
  	
  	public int score (String def1, String def2){
		String motCourant1, motCourant2;
		int i=0, score=0, k, m, n;
		boolean egaux=false;
		while(i<def1.length()){
			//On se place sur le debut d'un mot
			while(i<def1.length() && (def1.charAt(i) < 97 || def1.charAt(i) > 122)){
				//Si c'est une majuscule, on le remplace par une minuscule
				if(def1.charAt(i) >= 65 && def1.charAt(i) <= 90){
					def1.replace(def1.charAt(i), (char)(def1.charAt(i)+32));
				}else{
					i++;
				}
			}
			//On charche la fin du mot
			k=i+1;
			while(k<def1.length() && def1.charAt(k) >= 97 && def1.charAt(i) <= 122){
				k++;
			}
			egaux = false;
			//System.out.println(i+" "+k+" "+def1.length());
			if(k>=def1.length()){
				motCourant1=def1.substring(i);
			}else{
				motCourant1=def1.substring(i, k);
			}
			m=0;
			//egaux = false;
			while(m<def2.length() && !egaux){
				//On se place sur le debut d'un mot
				while(m<def2.length() && (def2.charAt(m) < 97 || def2.charAt(m) > 122)){
					//Si c'est une majuscule, on le remplace par une minuscule
					if(def2.charAt(m) >= 65 && def2.charAt(m) <= 90){
						def2.replace(def2.charAt(m), (char)(def2.charAt(m)+32));
					}else{
						m++;
					}
				}
				n=m+1;
				while(n<def2.length() && def2.charAt(n) >= 97 && def2.charAt(n) <= 122){
					n++;
				}
				if(n>=def2.length()){
					motCourant2=def2.substring(m);
				}else{
					motCourant2=def2.substring(m, n);
				}
				//Si les deux mots sont egaux, on les supprimes, on incremente le score, et on passe au mot suivant
				if(!motCourant1.equals("") && motCourant1.equals(motCourant2)){
					score++;
					egaux = true;
					if((i-1)>0){
						def1=def1.substring(0, (i-1))+def1.substring(k, def1.length());
					}else{
						def1=def1.substring(k, def1.length());
					}
					if((m-1)>0){
						def2=def2.substring(0, (m-1))+def2.substring(n, def2.length());
					}else{
						def2=def2.substring(n, def2.length());
					}
				}else{
					m=n;
				}
			}
			if(!egaux){
				i=k;
			}
		}
		return score;
	}

    private void addToSignature(StringSemanticSignature signature, String def) {
        StringTokenizer st = new StringTokenizer(def, " ", false);
        SnowballStemmer stemmer = new englishStemmer();
        int ww = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken().replaceAll("\"|;", "").toLowerCase();
            //Removing stop words from definitions
            if (!usesStopWords || !StopList.isStopWord(token)) {
                if (stemming) {
                    stemmer.setCurrent(token);
                    //stemmer.stem();
                    signature.addSymbol(stemmer.getCurrent(), 1.0);
                } else {
                    signature.addSymbol(token, 1.0);
                }
            }
        }
    }

    private int numberOfSenses(String word) {
        IIndexWord w = null;
        int senses = 0;
        w = dictionary.getIndexWord(word, POS.NOUN);
        if (w != null) {
            senses += w.getWordIDs().size();
        }
        w = dictionary.getIndexWord(word, POS.ADJECTIVE);
        if (w != null) {
            senses += w.getWordIDs().size();
        }
        w = dictionary.getIndexWord(word, POS.ADVERB);
        if (w != null) {
            senses += w.getWordIDs().size();
        }
        w = dictionary.getIndexWord(word, POS.VERB);
        if (w != null) {
            senses += w.getWordIDs().size();
        }
        return senses;
    }

    private IIndexWord getWord(String sid) {
        String lemme;
        String pos;
        String[] st = sid.split("%");
        if (sid.contains("%%n")) {
            lemme = "%";
            pos = "n";
        } else {
            lemme = st[0].toLowerCase();
            pos = st[1];
        }
        IIndexWord w = null;
        if (!lemme.isEmpty()) {
            if (pos.toLowerCase().startsWith("n")) {
                w = dictionary.getIndexWord(lemme, POS.NOUN);
            } else if (pos.toLowerCase().startsWith("v")) {
                w = dictionary.getIndexWord(lemme, POS.VERB);
            } else if (pos.toLowerCase().startsWith("a")) {
                w = dictionary.getIndexWord(lemme, POS.ADJECTIVE);
            } else if (pos.toLowerCase().startsWith("r")) {
                w = dictionary.getIndexWord(lemme, POS.ADVERB);
            }
        }
        return w;
    }

    public LRLoader extendedSignature(boolean hasExtendedSignature) {
        this.hasExtendedSignature = hasExtendedSignature;
        return this;
    }

    @Override
    public void loadSenses(Document document, SemecoreDefinitionExpender definitionExpender, int profondeur, DSODefinitionExpender contexteDSO){
    	List<Sense> senses;
    	System.out.println("jambon 0.2");
    	for (Word w : document) {
        	senses=getSenses(w);
        	if(definitionExpender!=null){
        		senses=getSensesEnrichisSemecore(w, senses, definitionExpender, profondeur);
        	}
        	if(contexteDSO!=null){
        		senses=getSensesEnrichisDSO(w, senses, contexteDSO);
        	}
            document.addWordSenses(senses);
        }
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader shuffle(boolean shuffle) {
        this.shuffle = shuffle;
        return this;
    }

    @Override
    public LRLoader loadDefinitions(boolean loadDefinitions) {
        this.loadDefinitions = loadDefinitions;
        return this;
    }

    @Override
    public LRLoader setLoadRelated(boolean loadRelated) {
        this.loadRelated = loadRelated;
        return this;
    }

    @Override
    public WordnetLoader setStemming(boolean stemming) {
        this.stemming = stemming;
        return this;
    }

    @Override
    public WordnetLoader setUsesStopWords(boolean usesStopWords) {
        this.usesStopWords = usesStopWords;
        return this;
    }

	@Override
	public void loadSenses(Document document) {
		// TODO Auto-generated method stub
		
	}
}