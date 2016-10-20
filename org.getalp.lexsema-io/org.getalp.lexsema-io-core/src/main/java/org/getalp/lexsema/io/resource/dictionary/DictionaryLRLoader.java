package org.getalp.lexsema.io.resource.dictionary;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.thesaurus.AnnotatedTextThesaurus;
import org.getalp.lexsema.similarity.*;
import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.enrichment.SignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndexImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.StopList;
import org.getalp.lexsema.util.distribution.SparkSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("BooleanParameter")
public class DictionaryLRLoader implements LRLoader {

    private static final DocumentFactory DOCUMENT_FACTORY = DefaultDocumentFactory.DEFAULT_DOCUMENT_FACTORY;
    private static final Logger logger = LoggerFactory.getLogger(DictionaryLRLoader.class);

    private final Map<String, List<Sense>> wordSenses;
    private boolean useIndex;
    private final boolean indexed;
    private boolean distributed;

    private final SignatureEnrichment signatureEnrichment;
    private boolean usesStopWords;
    private boolean usesStemming;

    private final SymbolIndex symbolIndex;

    public DictionaryLRLoader(InputStream dictionaryFile) {
        this(dictionaryFile, true, false, null);
    }

    public DictionaryLRLoader(InputStream dictionaryFile, boolean indexed) {
        this(dictionaryFile, indexed, false);
    }

    public DictionaryLRLoader(InputStream dictionaryFile, boolean indexed, boolean vectorized) {
        this(dictionaryFile, indexed, vectorized, null);
    }

    public DictionaryLRLoader(InputStream dictionaryFile, boolean indexed, SignatureEnrichment signatureEnrichment) {
        this(dictionaryFile, indexed, false, null);
    }
    
    public DictionaryLRLoader(InputStream dictionaryFile, boolean indexed, boolean vectorized, SignatureEnrichment signatureEnrichment) {
        wordSenses = new HashMap<>();
        this.indexed = indexed;
        this.signatureEnrichment = signatureEnrichment;
        usesStopWords = false;
        usesStemming = false;
        symbolIndex = new SymbolIndexImpl();
        try {
            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setContentHandler(new DictionaryParser(wordSenses, indexed, vectorized));
            saxReader.parse(new InputSource(dictionaryFile));
        } catch (SAXException e) {
            logger.error(MessageFormat.format("Parser error :{0}", e.getLocalizedMessage()));
        } catch (FileNotFoundException e) {
            logger.error(MessageFormat.format("File not found :{0}", e.getLocalizedMessage()));
        } catch (IOException e) {
            logger.error(MessageFormat.format("Read|Write error :{0}", e.getLocalizedMessage()));
        }
    }


    @Override
    public List<Sense> getSenses(Word w) {
        List<Sense> senses = Collections.emptyList();
        String lemma = w.getLemma();
        String partOfSpeech = w.getPartOfSpeech();
        partOfSpeech = processPOS(partOfSpeech);
        if (!partOfSpeech.equals("x")) {
            String tag = MessageFormat.format("{0}%{1}", lemma.toLowerCase(), partOfSpeech);
            if (wordSenses.get(tag) != null) {
                tag = MessageFormat.format("{0}%{1}", lemma, partOfSpeech);
            }
            senses = wordSenses.get(tag);
            if (senses != null) {
                for (Sense sense : senses) {
                    if (!indexed) {
                        SemanticSignature semanticSignature = sense.getSemanticSignature();
                        if (usesStopWords) {
                            semanticSignature = removeStopWords(semanticSignature);
                        }
                        if (signatureEnrichment != null) {
                            semanticSignature = signatureEnrichment.enrichSemanticSignature(semanticSignature);
                        }
                        if (usesStemming) {
                            semanticSignature = stemSignatureWords(semanticSignature);
                        }
                        if (useIndex) {
                            semanticSignature = indexSignature(semanticSignature);
                        }
                        sense.setSemanticSignature(semanticSignature);
                    }
                }
            }
        }
        return senses;
    }

    private SemanticSignature indexSignature(Iterable<SemanticSymbol> signature) {
        IndexedSemanticSignature indexedSignature = DefaultSemanticSignatureFactory.DEFAULT.
                createIndexedSemanticSignature(symbolIndex);
        for (SemanticSymbol symbol : signature) {
            indexedSignature.addSymbol(symbol);
        }
        indexedSignature.sort();
        return indexedSignature;
    }

    private SemanticSignature removeStopWords(Iterable<SemanticSymbol> signature) {
        SemanticSignature newSignature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
        for (SemanticSymbol symbol : signature) {
            if (!StopList.isStopWord(symbol.getSymbol())) {
                newSignature.addSymbol(symbol);
            }
        }
        return newSignature;
    }

    private SemanticSignature stemSignatureWords(Iterable<SemanticSymbol> signature) {
        SemanticSignature newSignature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
        EnglishStemmer stemmer = new EnglishStemmer();
        for (SemanticSymbol symbol : signature) {
            stemmer.setCurrent(symbol.getSymbol());
            stemmer.stem();
            addSymbolToSignature(newSignature, stemmer.getCurrent());
        }
        return newSignature;
    }

    private void addSymbolToSignature(SemanticSignature semanticSignature, String semanticSymbol){
        semanticSignature.addSymbol(semanticSymbol);
    }

    @Override
    public Map<Word, List<Sense>> getAllSenses() {
        Map<Word, List<Sense>> senses = new ConcurrentHashMap<>();

        wordSenses.keySet().parallelStream().forEach(word -> {
            String[] idParts = word.split("%");
            Word w = DOCUMENT_FACTORY.createWord(word, idParts[0], idParts[0], idParts[1]);
            senses.put(w, wordSenses.get(word));
        });
        return senses;
    }

    @Override
    public void loadSenses(Document document) {
        //noinspection LocalVariableOfConcreteClass
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

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader shuffle(boolean shuffle) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader extendedSignature(boolean hasExtendedSignature) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader loadDefinitions(boolean loadDefinitions) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader loadRelated(boolean loadRelated) {
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader stemming(boolean stemming) {
        usesStemming = stemming;
        return this;
    }

    @SuppressWarnings("BooleanParameter")
    @Override
    public LRLoader filterStopWords(boolean usesStopWords) {
        this.usesStopWords = usesStopWords;
        return this;
    }

    @Override
    public LRLoader addThesaurus(AnnotatedTextThesaurus thesaurus) {
        return this;
    }

    @Override
    public LRLoader index(boolean useIndex) {
        this.useIndex = useIndex;
        return this;
    }

    @Override
    public LRLoader distributed(boolean isDistributed) {
        distributed = isDistributed;
        return this;
    }
    
    private String processPOS(String pos) {
        char newPos = 'n';
        String lpos = pos.toLowerCase();
        if (lpos.startsWith("n") || lpos.startsWith("v") || lpos.startsWith("r")) {
            newPos = lpos.charAt(0);
        } else if (lpos.startsWith("j") || lpos.startsWith("a")) {
            newPos = 'a';
        } else {
        	newPos = 'x'; //< x means "wrong", "bad", "something else", "not a wordnet postag" 
        }
        return String.valueOf(newPos);
    }
}
