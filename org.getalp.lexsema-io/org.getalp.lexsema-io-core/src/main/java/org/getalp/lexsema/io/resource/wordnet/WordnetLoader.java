package org.getalp.lexsema.io.resource.wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.*;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurus;
import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.similarity.cache.SenseCache;
import org.getalp.lexsema.similarity.cache.SenseCacheImpl;
import org.getalp.lexsema.similarity.signatures.*;
import org.getalp.lexsema.similarity.signatures.enrichment.SignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndexImpl;
import org.getalp.lexsema.util.StopList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.ext.EnglishStemmer;

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

    private final SignatureEnrichment signatureEnrichment;

    private boolean loadDefinitions;

    private boolean loadRelated;

    private boolean hasExtendedSignature;

    private boolean usesStopWords;

    private boolean usesStemming;

    private boolean useIndex;

    private boolean shuffle;

    private final SymbolIndex symbolIndex;

    private final List<AnnotatedTextThesaurus> thesauri;

    private final Map<String, List<Sense>> senseCache;

    /**
     * Creates a WordnetLoader with an existing Wordnet Dictionary object.
     * The dictionary may or may not be open prior to this constructor call.
     * In every cases, it is opened during the call.
     */
    public WordnetLoader(Dictionary dictionary) {
        this.dictionary = dictionary;
        openDictionary();
        symbolIndex = new SymbolIndexImpl();
        loadDefinitions = true;
        loadRelated = false;
        hasExtendedSignature = false;
        usesStopWords = false;
        usesStemming = false;
        shuffle = false;
        //noinspection all
        thesauri = new ArrayList<AnnotatedTextThesaurus>();
        useIndex = false;
        signatureEnrichment = null;
        senseCache = new HashMap<>();
    }

    public WordnetLoader(Dictionary dictionary, SignatureEnrichment signatureEnrichment) {
        this.dictionary = dictionary;
        this.signatureEnrichment = signatureEnrichment;
        openDictionary();
        symbolIndex = new SymbolIndexImpl();
        loadDefinitions = true;
        loadRelated = false;
        hasExtendedSignature = false;
        usesStopWords = false;
        usesStemming = false;
        shuffle = false;
        //noinspection all
        thesauri = new ArrayList<AnnotatedTextThesaurus>();
        useIndex = false;
        senseCache = new HashMap<>();
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

    private SemanticSignature createSignature() {
        if (useIndex) {
            return new IndexedSemanticSignatureImpl(symbolIndex);
        } else {
            return new SemanticSignatureImpl();
        }
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

                    for (AnnotatedTextThesaurus thesaurus : thesauri) {
                        String senseKeyString = senseKey.toString();
                        List<String> relatedWords = thesaurus.getRelatedWords(senseKeyString);
                        for (String relatedWord : relatedWords) {
                            addToSignature(signature, relatedWord);
                        }
                    }

                    if (signatureEnrichment != null) {
                        signatureEnrichment.enrichSemanticSignature(signature);
                        logger.trace("\tEnritching a semantic signature...");
                    }

                    if (useIndex) {
                        ((IndexedSemanticSignature) signature).sort();
                    }
                    sense.setSemanticSignature(signature);

                    senses.add(sense);
                }

            }
        }
        logger.trace("Senses for {} processed and enritched.", lemma);
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
        final SenseCache senseCache = getSenseCache();
        List<Sense> senses;
        senses = retrieveSenseFromCache(w, senseCache);
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
            commitSensesToCache(w, senses, senseCache);
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

    private void appendToSignature(SemanticSignature semanticSignature, SemanticSignature other) {
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
    public LRLoader addThesaurus(AnnotatedTextThesaurus thesaurus) {
        thesauri.add(thesaurus);
        return this;
    }

    @Override
    public LRLoader index(boolean useIndex) {
        this.useIndex = useIndex;
        return this;
    }

    @Override
    public void loadSenses(Document document) {

        try(IntStream range = IntStream.range(0, document.size())) {
            try(IntStream parallelRange = range.parallel()) {
                List<List<Sense>> senses = parallelRange
                        .mapToObj(i -> getSenses(document.getWord(i)))
                        .collect(Collectors.toList());
                senses.forEach(document::addWordSenses);
            }
        }

    }
}