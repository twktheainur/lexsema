package org.getalp.lexsema.io.resource.wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.*;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurus;
import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.cache.SenseCache;
import org.getalp.lexsema.similarity.cache.SenseCacheImpl;
import org.getalp.lexsema.similarity.signatures.*;
import org.getalp.lexsema.similarity.signatures.enrichment.IndexingSignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.enrichment.SignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.enrichment.StemmingSignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.enrichment.StopwordsRemovingSignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndexImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.distribution.SparkSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WordnetLoader implements LRLoader {

    private static final Logger logger = LoggerFactory.getLogger(WordnetLoader.class);

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static final Pattern NON_LETTERS = Pattern.compile("[^a-zA-Z ]");

    private final Dictionary dictionary;

    private List<SignatureEnrichment> signatureEnrichments;

    private boolean loadDefinitions;

    private boolean loadRelated;

    private boolean hasExtendedSignature;

    private boolean shuffle;

    private final List<AnnotatedTextThesaurus> thesauri;

    private final Map<String, List<Sense>> senseCache;
    
    private boolean distributed;

    private List<List<String>> senseClusters;
    
    /**
     * Creates a WordnetLoader with an existing Wordnet Dictionary object.
     * The dictionary may or may not be open prior to this constructor call.
     * In every cases, it is opened during the call.
     */
    public WordnetLoader(Dictionary dictionary) {
        this.dictionary = openDictionary(dictionary);
        signatureEnrichments = new ArrayList<>();
        loadDefinitions = true;
        loadRelated = false;
        hasExtendedSignature = false;
        shuffle = false;
        thesauri = new ArrayList<>();
        senseCache = new HashMap<>();
        distributed = false;
        senseClusters = null;
    }

    private Dictionary openDictionary(Dictionary dictionary) {
        if (dictionary != null && !dictionary.isOpen()) {
            try {
                dictionary.open();
            } catch (IOException e) {
                logger.info(e.getLocalizedMessage());
            }
        }
        return dictionary;
    }

    private SemanticSignature createSignature() {
        return new SemanticSignatureImpl();
    }

    private String processPOS(String pos) {
        char newPos = 'n';
        String lpos = pos.toLowerCase();
        if (lpos.startsWith("n") || lpos.startsWith("v") || lpos.startsWith("r")) {
            newPos = lpos.charAt(0);
        } else if (pos.startsWith("j") || pos.startsWith("a")) {
            newPos = 'a';
        }
        return String.valueOf(newPos);
    }

    private List<Sense> getSenses(String lemma, String pos) {
        List<Sense> senses;
        String id = MessageFormat.format("{0}%{1}", lemma, processPOS(pos));
        if (senseCache.containsKey(id)) {
            senses = senseCache.get(id);
        } else {
            senses = new ArrayList<>();

            IIndexWord iw = getWord(id);
            if (iw != null) {
                List<IWordID> wordIDs = iw.getWordIDs();
                for (IWordID wordID : wordIDs) {
                    IWord word = dictionary.getWord(wordID);
                    ISenseKey senseKey = word.getSenseKey();
                    Sense sense = new SenseImpl(senseKey.toString());
                    SemanticSignature signature = createSignature();
                    final ISynset wordSynset = word.getSynset();
                    if (loadDefinitions) {
                        String def = wordSynset.getGloss();
                        addToSignature(signature, def);
                    }

                    if (loadRelated || hasExtendedSignature) {
                        // Lexical relations are bound to IWord and are common to all associated synsets
                        loadLexicalRelations(sense, signature, word);

                        // Semantic relations are bound to ISynset and are specific to each synset
                        loadSemanticRelations(sense, signature, wordSynset);
                    }

                    for (AnnotatedTextThesaurus thesaurus : thesauri) {
                        String senseKeyString = senseKey.toString();
                        List<String> relatedWords = thesaurus.getRelatedWords(senseKeyString);
                        for (String relatedWord : relatedWords) {
                            addToSignature(signature, relatedWord);
                        }
                    }
                    
                    for (SignatureEnrichment signatureEnrichment : signatureEnrichments) {
                        signature = signatureEnrichment.enrichSemanticSignature(signature);
                    }
          
                    sense.setSemanticSignature(signature);
                    senses.add(sense);
                }
            }
            senseCache.put(id, senses);
        }
        return senses;
    }

    private void loadSemanticRelations(Sense sense, SemanticSignature semanticSignature, ISynset wordSynset) {
        Map<IPointer, List<ISynsetID>> rm = wordSynset.getRelatedMap();
        for (Map.Entry<IPointer, List<ISynsetID>> iPointerListEntry : rm.entrySet()) {
            for (ISynsetID iwd : iPointerListEntry.getValue()) {
                SemanticSignature localSignature = createSignature();
                ISynset synset = dictionary.getSynset(iwd);
                addToSignature(localSignature, synset.getGloss());
                if (hasExtendedSignature) {
                    appendToSignature(semanticSignature, localSignature);
                }
                if (loadRelated) {
                    IPointer key = iPointerListEntry.getKey();
                    sense.addRelatedSignature(key.getSymbol(), localSignature);
                }
            }
        }
    }

    private void loadLexicalRelations(Sense sense, SemanticSignature semanticSignature, IWord word) {
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
                if (loadRelated) {
                    final IPointer key = iPointerListEntry.getKey();
                    sense.addRelatedSignature(key.getSymbol(), localSignature);
                }
            }
        }
    }

    @Override
    public List<Sense> getSenses(Word w) {
        final SenseCache localSenseCache = getSenseCache();
        List<Sense> senses;
        senses = retrieveSenseFromCache(w, localSenseCache);
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
            if (senseClusters != null) {
                senses = clusterize(senses);
            }
            if (shuffle) {
                Collections.shuffle(senses);
            }
            commitSensesToCache(w, senses, localSenseCache);
        }
        return senses;
    }

    @Override
    public Map<Word, List<Sense>> getAllSenses() {
        Map<Word, List<Sense>> senses = new ConcurrentHashMap<>();
        Iterator<IIndexWord> nounIIndexWordIterator = dictionary.getBackingDictionary().getIndexWordIterator(POS.NOUN);
        Iterator<IIndexWord> adjectiveIIndexWordIterator = dictionary.getBackingDictionary().getIndexWordIterator(POS.ADJECTIVE);
        Iterator<IIndexWord> adverbIIndexWordIterator = dictionary.getBackingDictionary().getIndexWordIterator(POS.ADVERB);
        Iterator<IIndexWord> verbIIndexWordIterator = dictionary.getBackingDictionary().getIndexWordIterator(POS.VERB);

        Consumer<IIndexWord> processor = iidx -> {
            List<Sense> senseList = getSenses(iidx.getLemma(), String.valueOf(iidx.getPOS().getTag()));
            Word word = new WordImpl(iidx.getID().toString(), iidx.getLemma(), iidx.getLemma(), String.valueOf(iidx.getPOS().getTag()));
            senses.put(word, senseList);
        };
        nounIIndexWordIterator.forEachRemaining(processor);
        verbIIndexWordIterator.forEachRemaining(processor);
        adjectiveIIndexWordIterator.forEachRemaining(processor);
        adverbIIndexWordIterator.forEachRemaining(processor);

        return senses;
    }

    private SenseCache getSenseCache() {
        return SenseCacheImpl.getInstance();
    }

    private List<Sense> retrieveSenseFromCache(Word w, SenseCache senseCache) {
        return senseCache.getSenses(w);
    }

    private void commitSensesToCache(Word w, List<Sense> senses, SenseCache senseCache) {
        senseCache.addToCache(w, senses);
    }

    private void addToSignature(SemanticSignature signature, CharSequence def) {
        final Matcher matcher = NON_LETTERS.matcher(def);
        String noPunctuation = matcher.replaceAll("");
        String[] words = WHITESPACE.split(noPunctuation.toLowerCase());
        for (String token : words) {
            signature.addSymbol(token);
        }
    }

    private void appendToSignature(SemanticSignature semanticSignature, SemanticSignature other) {
        semanticSignature.appendSignature(other);
    }
    
    private List<Sense> clusterize(List<Sense> senses) {
        Map<String, List<Sense>> clusteredSenses = new HashMap<>();
        List<Sense> newSenses = new ArrayList<>();
        for (Sense sense : senses) {
            boolean inACluster = false;
            for (List<String> cluster : senseClusters) {
                for (String senseInCluster : cluster) {
                    if (sense.getId().equals(senseInCluster)) {
                        if (clusteredSenses.containsKey(cluster.get(0))) {
                            clusteredSenses.get(cluster.get(0)).add(sense);
                        }
                        else {
                            clusteredSenses.put(cluster.get(0), new ArrayList<Sense>(Arrays.asList(sense)));
                        }
                        inACluster = true;
                    }
                }
            }
            if (!inACluster) {
                newSenses.add(sense);
            }
        }
        for (String clusteredSense : clusteredSenses.keySet()) {
            Sense newSense = new SenseImpl(clusteredSense);
            SemanticSignature newSignature = new SemanticSignatureImpl();
            for (Sense senseInCluster : clusteredSenses.get(clusteredSense)) {
                newSignature.addSymbols(senseInCluster.getSemanticSignature().getSymbols());
            }
            newSense.setSemanticSignature(newSignature);
            newSenses.add(newSense);
        }
        return newSenses;
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
        POS posJWI = POS.getPartOfSpeech(pos.charAt(0));
        IIndexWord w = null;
        if (!lemme.isEmpty()) {

            if (posJWI != null) {
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
    public LRLoader addThesaurus(AnnotatedTextThesaurus thesaurus) {
        thesauri.add(thesaurus);
        return this;
    }
    
    public LRLoader addSignatureEnrichment(SignatureEnrichment signatureEnrichment) {
    	signatureEnrichments.add(signatureEnrichment);
    	return this;
    }

    @Override
    public LRLoader distributed(boolean isDistributed) {
        distributed = isDistributed;
        return this;
    }

    @Override
    public void loadSenses(Document document) {
        List<List<Sense>> senses;
        if (distributed) {
            senses = loadSensesDistributed(document);
        } else {
            try (IntStream range = IntStream.range(0, document.size())) {
                senses = range
                        .mapToObj(i -> getSenses(document.getWord(i)))
                        .collect(Collectors.toList());

            }
        }
        senses.forEach(document::addWordSenses);
    }

    @SuppressWarnings({"LocalVariableOfConcreteClass", "LawOfDemeter", "resource"})
    private List<List<Sense>> loadSensesDistributed(Iterable<Word> document) {
        List<List<Sense>> uniqueWordSenses;
        JavaSparkContext sparkContext = SparkSingleton.getSparkContext();
        Map<Word,Integer> wordIndexMap = new HashMap<>();
        List<Word> wordsToProcess = new ArrayList<>();
        int uniqueWordIndex = 0;
        for (Word word: document) {
            if(!wordIndexMap.containsKey(word)){
                wordIndexMap.put(word,uniqueWordIndex);
                wordsToProcess.add(word);
                uniqueWordIndex++;
            }
        }

        JavaRDD<Word> parallelSenses = sparkContext.parallelize(wordsToProcess);
        parallelSenses.cache();
        uniqueWordSenses = parallelSenses.map(this::getSenses).collect();

        List<List<Sense>> documentSenses = new ArrayList<>();
        for(Word word: document){
            List<Sense> currentWordSenses = uniqueWordSenses.get(wordIndexMap.get(word));
            documentSenses.add(currentWordSenses);
        }

        return documentSenses;
    }

    @Override
    public LRLoader stemming(boolean stemming) {
        if (stemming) {
            addSignatureEnrichment(new StemmingSignatureEnrichment());
        }
        return this;
    }

    @Override
    public LRLoader filterStopWords(boolean usesStopWords) {
        if (usesStopWords) {
            addSignatureEnrichment(new StopwordsRemovingSignatureEnrichment());
        }
        return this;
    }

    @Override
    public LRLoader index(boolean useIndex) {
        if (useIndex) {
            addSignatureEnrichment(new IndexingSignatureEnrichment());
        }
        return this;
    }
    
    public LRLoader setSenseClusters(List<List<String>> senseClusters) {
        this.senseClusters = senseClusters;
        return this;
    }

}