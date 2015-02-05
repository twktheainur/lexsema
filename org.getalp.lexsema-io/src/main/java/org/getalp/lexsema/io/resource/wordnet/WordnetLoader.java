package org.getalp.lexsema.io.resource.wordnet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.SenseImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.cache.SenseCacheImpl;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.util.StopList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

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

                SemanticSignature signature = new SemanticSignatureImpl();
                IWord word = dictionary.getWord(iw.getWordIDs().get(j));
                if(loadDefinitions) {
                    String def = word.getSynset().getGloss();
                    addToSignature(signature, def);
                }

                Sense s = new SenseImpl(word.getSenseKey().toString());

                if(loadRelated) {
                    Map<IPointer, List<IWordID>> rm = word.getRelatedMap();
                    for (IPointer p : rm.keySet()) {
                        for (IWordID iwd : rm.get(p)) {
                            SemanticSignature localSignature = new SemanticSignatureImpl();
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

    private void addToSignature(SemanticSignature signature, String def) {
        StringTokenizer st = new StringTokenizer(def, " ", false);
        SnowballStemmer stemmer = new englishStemmer();
        int ww=0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken().replaceAll("\"|;", "").toLowerCase();
            //Removing stop words from definitions
	        if (!usesStopWords || !StopList.isStopWord(token)) {
	           	if (stemming) {
		           	stemmer.setCurrent(token);
		            stemmer.stem();
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
    public void loadSenses(Document document) {
        for (Word w : document) {
            document.addWordSenses(getSenses(w));
        }
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader suffle(boolean shuffle) {
        this.shuffle = shuffle;
        return this;
    }

    @Override
    public LRLoader loadDefinition(boolean loadDefinitions) {
        this.loadDefinitions = loadDefinitions;
        return this;
    }

    @Override
    public LRLoader setLoadRelated(boolean loadRelated) {
        this.loadRelated = loadRelated;
        return this;
    }
    
    public WordnetLoader setStemming(boolean stemming) {
        this.stemming = stemming;
        return this;
    }
    
    public WordnetLoader setUsesStopWords(boolean usesStopWords) {
        this.usesStopWords = usesStopWords;
        return this;
    }
}
