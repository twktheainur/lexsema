package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.util.word2vec.Word2VecClient;


import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbolImpl;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.VectorOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Word2VecSignatureEnrichment4 extends SignatureEnrichment {

    private final int topN;
    
    public Word2VecSignatureEnrichment4(int topN) {
        this.topN = topN;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        List<double[]> symbolsVectors = new ArrayList<>();
        for (SemanticSymbol semanticSymbol : semanticSignature) {
            double[] symbolVector = Word2VecClient.getWordVector(semanticSymbol.getSymbol());
            if (symbolVector.length > 0) {
                symbolsVectors.add(symbolVector);
            }
        }
        if (symbolsVectors.size() == 0) return semanticSignature;
        double[] sum = VectorOperation.sum(symbolsVectors.toArray(new double[symbolsVectors.size()][]));
        sum = VectorOperation.normalize(sum);
        
        List<List<SemanticSymbol>> newSymbols = new ArrayList<>();
        for (SemanticSymbol symbol : semanticSignature) {
            newSymbols.add(enrichSemanticSymbol(symbol, sum));
        }
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (String word : semanticSignature.getStringSymbols()) {
            newSignature.addSymbol(word);
        }
        for (List<SemanticSymbol> semanticSymbols : newSymbols) {
            for (SemanticSymbol semanticSymbol : semanticSymbols) {
                newSignature.addSymbol(semanticSymbol);
            }
        }
        
        return newSignature;
    }
    
    private List<SemanticSymbol> enrichSemanticSymbol(SemanticSymbol semanticSymbol, double[] context) {
        List<SemanticSymbol> ret = new ArrayList<>();
        Collection<String> words = Word2VecClient.getMostSimilarWords(semanticSymbol.getSymbol(), topN, context);
        for (String word : words) {
            ret.add(new SemanticSymbolImpl(word, 1));
        }
        return ret;
    }
}
