package org.getalp.lexsema.io.resource.wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.*;

import org.getalp.lexsema.io.DSODefinitionExpender.DSODefinitionExpender;
import org.getalp.lexsema.io.definitionenricher.TextDefinitionEnricher;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.SenseImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.cache.SenseCacheImpl;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.getalp.lexsema.util.StopList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.ext.EnglishStemmer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

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
    
    private boolean usesIndex;
    private BiMap<String, Integer> indexMap;
    private int currentIndex;
    
    private boolean usesSemCor;
    private TextDefinitionEnricher semCorExpender;
    private int semCorNumberOfWords = 10;

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
        indexMap = HashBiMap.create();
        currentIndex = 0;
    }

    private List<Sense> getSenses(String lemma, String pos)
    {
        List<Sense> senses = new ArrayList<>();
        IIndexWord iw = getWord(lemma + "%" + pos);
        if (iw != null)
        {
            for (int j = 0; j < iw.getWordIDs().size(); j++)
            {
                IWord word = dictionary.getWord(iw.getWordIDs().get(j));
                Sense s = new SenseImpl(word.getSenseKey().toString());

                SemanticSignature signature;
                if (usesIndex) signature = new IndexedSemanticSignatureImpl();
                else signature = new StringSemanticSignatureImpl();
                
                if (loadDefinitions)
                {
                    String def = word.getSynset().getGloss();
                    addToSignature(signature, def);
                }

                if (loadRelated)
                {
                    Map<IPointer, List<ISynsetID>> rm = word.getSynset().getRelatedMap();
                    for (IPointer p : rm.keySet())
                    {
                        for (ISynsetID iwd : rm.get(p))
                        {
                            if (usesIndex)
                            {
                                IndexedSemanticSignature localSignature = new IndexedSemanticSignatureImpl();
                                addToSignature(localSignature, dictionary.getSynset(iwd).getGloss());
                                if (hasExtendedSignature)
                                {
                                    ((IndexedSemanticSignatureImpl) signature).appendSignature(localSignature);
                                }
                                s.addRelatedSignature(p.getSymbol(), localSignature);
                            }
                            else
                            {
                                StringSemanticSignature localSignature = new StringSemanticSignatureImpl();
                                addToSignature(localSignature, dictionary.getSynset(iwd).getGloss());
                                if (hasExtendedSignature)
                                {
                                    ((StringSemanticSignature) signature).appendSignature(localSignature);
                                }
                                s.addRelatedSignature(p.getSymbol(), localSignature);
                            }
                        }
                    }
                    
                    Map<IPointer, List<IWordID>> rm2 = word.getRelatedMap();
                    for (IPointer p : rm2.keySet())
                    {
                        for (IWordID iwd : rm2.get(p))
                        {
                            if (usesIndex)
                            {
                                IndexedSemanticSignature localSignature = new IndexedSemanticSignatureImpl();
                                addToSignature(localSignature, dictionary.getWord(iwd).getSynset().getGloss());
                                if (hasExtendedSignature)
                                {
                                    ((IndexedSemanticSignatureImpl) signature).appendSignature(localSignature);
                                }
                                s.addRelatedSignature(p.getSymbol(), localSignature);
                            }
                            else
                            {
                                StringSemanticSignature localSignature = new StringSemanticSignatureImpl();
                                addToSignature(localSignature, dictionary.getWord(iwd).getSynset().getGloss());
                                if (hasExtendedSignature)
                                {
                                    ((StringSemanticSignature) signature).appendSignature(localSignature);
                                }
                                s.addRelatedSignature(p.getSymbol(), localSignature);
                            }
                        }
                    }
                }
                
                if (usesSemCor && semCorExpender != null)
                {
                    String senseKeyString = word.getSenseKey().toString();
                    String[] relatedWords = semCorExpender.getRelatedWords(senseKeyString, semCorNumberOfWords);
                    for (String relatedWord : relatedWords)
                    {
                        addToSignature(signature, relatedWord);
                    }
                }
                

                if (usesIndex)
                {
                    ((IndexedSemanticSignatureImpl) signature).sort();
                }
                
                s.setSemanticSignature(signature);
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
        String[] words = def.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        EnglishStemmer stemmer = new EnglishStemmer();
        for (String token : words) {
            
            if (usesStopWords && StopList.isStopWord(token)) {
                continue;
            }

            if (stemming) {
                stemmer.setCurrent(token);
                stemmer.stem();
                token = stemmer.getCurrent();
            }

            if (usesIndex) {
                IndexedSemanticSignature indexedSignature = (IndexedSemanticSignature) signature;
                if (!indexMap.containsKey(token)) {
                    indexMap.put(token, currentIndex++);
                }
                indexedSignature.addSymbol(indexMap.get(token));
            }
            else {
                StringSemanticSignature normalSignature = (StringSemanticSignature) signature;
                normalSignature.addSymbol(token);
            }
        }
    }

    public int numberOfSenses(String word) {
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
    public void loadSenses(Document document, TextDefinitionEnricher definitionExpender, int profondeur, DSODefinitionExpender contexteDSO){
        for (Word w : document) {
            document.addWordSenses(getSenses(w));
        }
    }

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
    
    public WordnetLoader setUsesIndex(boolean usesIndex) {
        this.usesIndex = usesIndex;
        return this;
    }
    
    public WordnetLoader setUsesSemCor(boolean usesSemCor) {
        this.usesSemCor = usesSemCor;
        return this;
    }
    
    public WordnetLoader setSemCorDefinitionExpender(TextDefinitionEnricher semCorExpender) {
        this.semCorExpender = semCorExpender;
        return this;
    }
    
    public WordnetLoader setSemCorNumberOfWordsToTake(int numberOfWords) {
        this.semCorNumberOfWords = numberOfWords;
        return this;
    }

	@Override
	public void loadSenses(Document document) {
        for (Word w : document) {
            document.addWordSenses(getSenses(w));
        }
	}
}