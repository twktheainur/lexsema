package org.getalp.lexsema.similarity.signatures.enrichment;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.DefaultSemanticSymbolFactory;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Word2VecLocalSignatureEnrichment extends SignatureEnrichmentAbstract {

    public static final int DEFAULT_TOP_N = 10;
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{Punct}");

    private final WordVectors word2Vec;
    private final int topN;
    
    private static final Map<String, List<SemanticSymbol>> symbolsCache = new HashMap<>();


    public Word2VecLocalSignatureEnrichment(WordVectors word2Vec) {
        this(word2Vec, DEFAULT_TOP_N);
    }

    public Word2VecLocalSignatureEnrichment(WordVectors word2Vec, int topN) {
        this.word2Vec = word2Vec;
        this.topN = topN;
    }

    private List<SemanticSymbol> enrichSemanticSymbol(SemanticSymbol semanticSymbol) {
        String word = semanticSymbol.getSymbol();
        if (symbolsCache.containsKey(word) && symbolsCache.get(word).size() >= topN) {
            return symbolsCache.get(word).subList(0, topN);
        }
        final Matcher matcher = PUNCTUATION_PATTERN.matcher(word);
        Collection<String> related = word2Vec.wordsNearest(matcher.replaceAll(""), topN);
        List<String> relatedSorted = sortRelatedList(word, related);
        List<SemanticSymbol> symbols = new ArrayList<>();
        symbols.add(semanticSymbol);
        for (String sword : relatedSorted) {
            symbols.add(DefaultSemanticSymbolFactory.DEFAULT_FACTORY.createSemanticSymbol(sword));
        }
        symbolsCache.put(word, symbols);
        return symbols;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        SemanticSignature newSignature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
        for (SemanticSymbol semanticSymbol : semanticSignature) {
            newSignature.addSymbols(enrichSemanticSymbol(semanticSymbol));
        }
        return newSignature;
    }

    private List<String> sortRelatedList(String word, Collection<String> related) {
        List<String> relatedSorted = new ArrayList<>(related);
        relatedSorted.sort((arg0, arg1) -> {
            if (word2Vec.similarity(word, arg0) > word2Vec.similarity(word, arg1)) {
                return -1;
            } else {
                return 1;
            }
        });
        return relatedSorted;
    }
}
