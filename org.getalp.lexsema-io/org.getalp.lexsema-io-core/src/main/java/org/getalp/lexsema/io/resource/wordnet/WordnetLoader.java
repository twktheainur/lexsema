package org.getalp.lexsema.io.resource.wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.*;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurus;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.SenseImpl;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.cache.SenseCache;
import org.getalp.lexsema.similarity.cache.SenseCacheImpl;
import org.getalp.lexsema.similarity.signatures.*;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndexImpl;
import org.getalp.lexsema.util.StopList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.ext.EnglishStemmer;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordnetLoader implements LRLoader {
    private static final Logger logger = LoggerFactory.getLogger(WordnetLoader.class);
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern NON_LETTERS = Pattern.compile("[^(\\p{L}|\\p{N}) ]");

    private final Dictionary dictionary;

    private boolean loadDefinitions = true;

    private boolean loadRelated;

    private boolean hasExtendedSignature;

    private boolean usesStopWords;

    private boolean usesStemming;

    private boolean useIndex;

    private boolean shuffle;

    private final SymbolIndex symbolIndex;

    private AnnotatedTextThesaurus semCorExpander;




    /**
     * Creates a WordnetLoader2 with an existing Wordnet Dictionary object.
     * The dictionary may or may not be open prior to this constructor call.
     * In every cases, it is opened during the call.
     */
    public WordnetLoader(Dictionary dictionary) {
        this.dictionary = dictionary;
        openDictionary();
        symbolIndex = new SymbolIndexImpl();

        loadRelated = false;
        hasExtendedSignature = false;
        usesStopWords = false;
        usesStemming = false;
        shuffle = false;
        //noinspection all
        semCorExpander = null;
        useIndex = false;
    }

    public WordnetLoader(Dictionary dictionary, AnnotatedTextThesaurus annotatedTextThesaurus) {
        this(dictionary);
        semCorExpander = annotatedTextThesaurus;
    }

    private void openDictionary() {
        if (dictionary != null && !dictionary.isOpen()) {
            try {
                dictionary.open();
            } catch (IOException e) {
                logger.info(e.getLocalizedMessage());
            }
        }
    }

    private SemanticSignature createSignature(){
        if(useIndex){
            return new IndexedSemanticSignatureImpl(symbolIndex);
        } else {
            return new SemanticSignatureImpl();
        }
    }

    private List<Sense> getSenses(String lemma, String pos) {
        List<Sense> senses = new ArrayList<>();
        IIndexWord iw = getWord(MessageFormat.format("{0}%{1}", lemma, pos));
        if (iw != null) {
            final List<IWordID> wordIDs = iw.getWordIDs();
            for (IWordID wordID : wordIDs) {
                IWord word = dictionary.getWord(wordID);
                final ISenseKey senseKey = word.getSenseKey();
                Sense sense = new SenseImpl(senseKey.toString());
                SemanticSignature signature = createSignature();
                final ISynset wordSynset = word.getSynset();
                if (loadDefinitions) {
                    String def = wordSynset.getGloss();
                    addToSignature(signature, def);
                }

                if (loadRelated || hasExtendedSignature) {

                    // Lexical relations are bound to IWord nd are common to all
                    // associated synsets
                    loadLexicalRelations(sense, signature, word);

                    // Semantic relations are bound to ISynset and are specific to each synset
                    loadSemanticRelations(sense, signature, wordSynset);
                }

                if (semCorExpander != null) {
                    String senseKeyString = senseKey.toString();
                    List<String> relatedWords = semCorExpander.getRelatedWords(senseKeyString);
                    for (String relatedWord : relatedWords) {
                        addToSignature(signature, relatedWord);
                    }
                }


                if(useIndex) {
                    ((IndexedSemanticSignature)signature).sort();
                }
                sense.setSemanticSignature(signature);

                senses.add(sense);
            }
        }
        return senses;
    }

    private void loadSemanticRelations(Sense sense, SemanticSignature semanticSignature, ISynset wordSynset){
        Map<IPointer, List<ISynsetID>> rm = wordSynset.getRelatedMap();
        for (Map.Entry<IPointer, List<ISynsetID>> iPointerListEntry : rm.entrySet()) {
            for (ISynsetID iwd : iPointerListEntry.getValue()) {
                SemanticSignature localSignature = createSignature();
                ISynset synset = dictionary.getSynset(iwd);
                addToSignature(localSignature, synset.getGloss());
                if (hasExtendedSignature) {
                    appendToSignature(semanticSignature, localSignature);
                }
                if(loadRelated) {
                    IPointer key = iPointerListEntry.getKey();
                    sense.addRelatedSignature(key.getSymbol(), localSignature);
                }

            }
        }
    }

    private void loadLexicalRelations(Sense sense, SemanticSignature semanticSignature, IWord word){
        Map<IPointer, List<IWordID>> rm2 = word.getRelatedMap();
        for (Map.Entry<IPointer, List<IWordID>> iPointerListEntry : rm2.entrySet()) {
            for (IWordID iwd : iPointerListEntry.getValue()) {
                SemanticSignature localSignature = createSignature();
                IWord iword = dictionary.getWord(iwd);
                ISynset synset = iword.getSynset();
                addToSignature(localSignature, synset.getGloss());
                if (hasExtendedSignature) {
                    semanticSignature.appendSignature(localSignature);
                }
                if(loadRelated) {
                    final IPointer key = iPointerListEntry.getKey();
                    sense.addRelatedSignature(key.getSymbol(), localSignature);
                }
            }
        }
    }


    @Override
    public List<Sense> getSenses(Word w) {
        final SenseCache senseCache = getSenseCache();
        List<Sense> senses;
        senses = retrieveSenseFromCache(w,senseCache);
        if (senses == null) {
            if (w != null) {
                String lemma = w.getLemma();
                String partOfSpeech = w.getPartOfSpeech();
                if (partOfSpeech == null || partOfSpeech.isEmpty()) {
                    senses = getSenses(lemma, "n");
                    senses.addAll(getSenses(lemma, "r"));
                    senses.addAll(getSenses(lemma, "a"));
                    senses.addAll(getSenses(lemma, "v"));
                } else {
                    senses = getSenses(lemma, partOfSpeech);
                }
            } else {
                senses = new ArrayList<>();
            }
            if (shuffle) {
                Collections.shuffle(senses);
            }
            commitSensesToCache(w,senses,senseCache);
        }
        return senses;
    }

    private SenseCache getSenseCache(){
        return SenseCacheImpl.getInstance();
    }

    private List<Sense> retrieveSenseFromCache(Word w, SenseCache senseCache){
        return senseCache.getSenses(w);
    }

    private void commitSensesToCache(Word w, List<Sense> senses, SenseCache senseCache){
        senseCache.addToCache(w,senses);
    }


    private void addToSignature(SemanticSignature signature, CharSequence def) {
        final Matcher matcher = NON_LETTERS.matcher(def);
        String noPunctuation = matcher.replaceAll("");
        String[] words = WHITESPACE.split(noPunctuation.toLowerCase());
        EnglishStemmer stemmer = new EnglishStemmer();
        for (String token : words) {

            if (usesStopWords && StopList.isStopWord(token)) {
                //noinspection ContinueStatement
                continue;
            }

            if (usesStemming) {
                stemmer.setCurrent(token);
                stemmer.stem();
                token = stemmer.getCurrent();
            }

            signature.addSymbol(token);
        }
    }

    private void appendToSignature(SemanticSignature semanticSignature, SemanticSignature other){
        semanticSignature.appendSignature(other);
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
            pos = st[1].toLowerCase();
        }
        IIndexWord w = null;
        if (!lemme.isEmpty()) {
            POS posJWI = POS.getPartOfSpeech(pos.charAt(0));
            if(posJWI!=null){
                w = dictionary.getIndexWord(lemme, posJWI);
            }
        }
        return w;
    }

    @Override
    public LRLoader extendedSignature(boolean hasExtendedSignature) {
        this.hasExtendedSignature = hasExtendedSignature;
        return this;
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
    public LRLoader loadRelated(boolean loadRelated) {
        this.loadRelated = loadRelated;
        return this;
    }

    @Override
    public LRLoader stemming(boolean stemming) {
        usesStemming = stemming;
        return this;
    }

    @Override
    public LRLoader filterStopWords(boolean usesStopWords) {
        this.usesStopWords = usesStopWords;
        return this;
    }

    @Override
    public LRLoader index(boolean useIndex){
        this.useIndex = useIndex;
        return  this;
    }

    @Override
    public void loadSenses(Document document) {
        for (Word w : document) {
            document.addWordSenses(getSenses(w));
        }
    }
}