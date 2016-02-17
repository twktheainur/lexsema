package org.getalp.lexsema.similarity.signatures.enrichment;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbolImpl;
import org.getalp.lexsema.util.Language;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Word2VecSignatureEnrichment2 implements SignatureEnrichment {

    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{Punct}");

    private final List<WordVectors> word2Vec;
    private final int topN;
    
    private static final ConcurrentHashMap<String, List<SemanticSymbol>> symbolsCache = new ConcurrentHashMap<>();

    public Word2VecSignatureEnrichment2(int topN) {
        word2Vec = new ArrayList<>();
        for (int i = 0 ; i < Runtime.getRuntime().availableProcessors() ; i++) {
            try {
                word2Vec.add(WordVectorSerializer.loadGoogleModel(new File("../data/word2vec/", "model.bin"), true, true));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        this.topN = topN;
    }

    private List<SemanticSymbol> enrichSemanticSymbol(SemanticSymbol semanticSymbol, WordVectors word2Vec) {
        String word = semanticSymbol.getSymbol();
        if (symbolsCache.containsKey(word)) {
            return symbolsCache.get(word);
        }
        final Matcher matcher = PUNCTUATION_PATTERN.matcher(word);
        Collection<String> related = word2Vec.wordsNearest(matcher.replaceAll(""), topN);
        List<SemanticSymbol> symbols = new ArrayList<>();
        symbols.add(semanticSymbol);
        for (String sword : related) {
            symbols.add(new SemanticSymbolImpl(sword, 1.0));
        }
        symbolsCache.put(word, symbols);
        return symbols;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        
        System.err.println("Enriching a semantic signature...");
        int nbWords = semanticSignature.size();
        int nbProcess = word2Vec.size();
        
        List<List<SemanticSymbol>> wordsPerThread = new ArrayList<>();
        for (int i = 0 ; i < nbProcess ; i++) {
            wordsPerThread.add(new ArrayList<>());
        }
        
        int order = 0;
        for (int currentWord = 0 ; currentWord < nbWords ; currentWord++) {
            wordsPerThread.get(order).add(semanticSignature.getSymbol(currentWord));
            order++;
            order %= nbProcess;
        }
                
        List<Thread> tasks = new ArrayList<>();
        
        for (int i = 0 ; i < nbProcess ; i++) {
            int ii = i;
            tasks.add(new Thread(){
                public void run() {
                    List<SemanticSymbol> originalList = new ArrayList<>(wordsPerThread.get(ii));
                    for (SemanticSymbol semanticSymbol : originalList) {
                        wordsPerThread.get(ii).addAll(enrichSemanticSymbol(semanticSymbol, word2Vec.get(ii)));
                    }
                } 
            });
        }
        
        for (Thread thread : tasks) {
            thread.start();
        }

        for (Thread thread : tasks) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }
        
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (List<SemanticSymbol> semanticSymbols : wordsPerThread) {
            for (SemanticSymbol semanticSymbol : semanticSymbols) {
                newSignature.addSymbol(semanticSymbol);
            }
        }
        return newSignature;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, Language language) {
        return null;
    }

    @Override
    public void close() {

    }

}
