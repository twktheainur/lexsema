package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.util.word2vec.Word2VecClient;


import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.VectorOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Word2VecSignatureEnrichment3 implements SignatureEnrichment {

    private final int topN;
    
    public Word2VecSignatureEnrichment3(int topN) {
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
        double[] sumNormalized = VectorOperation.normalize(sum);
        Collection<String> nearests = Word2VecClient.getMostSimilarWords(sumNormalized, topN);
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (String word : semanticSignature.getStringSymbols()) {
            newSignature.addSymbol(word);
        }
        for (String word : nearests) {
            newSignature.addSymbol(word);
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
