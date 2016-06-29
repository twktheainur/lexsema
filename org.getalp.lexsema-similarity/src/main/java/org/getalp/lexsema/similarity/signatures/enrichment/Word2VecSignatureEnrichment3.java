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

public class Word2VecSignatureEnrichment3 extends SignatureEnrichment {

    private final int topN;
    
    public Word2VecSignatureEnrichment3(int topN) {
        this.topN = topN;
    }

    private double[] constructSenseVector(SemanticSignature semanticSignature) {
    	double[] ret = null;
        for (SemanticSymbol semanticSymbol : semanticSignature) {
            double[] symbolVector = Word2VecClient.getWordVector(semanticSymbol.getSymbol());
            if (symbolVector.length == 0) continue;
            if (ret == null) ret = symbolVector;
            else ret = VectorOperation.add(ret, symbolVector);
        }
        if (ret != null) ret = VectorOperation.normalize(ret);
        return ret;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
    	double[] senseVector = constructSenseVector(semanticSignature);
    	if (senseVector == null) return semanticSignature;
        Collection<String> nearests = Word2VecClient.getMostSimilarWords(senseVector, topN);
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (String word : semanticSignature.getStringSymbols()) {
            newSignature.addSymbol(word);
        }
        for (String word : nearests) {
            newSignature.addSymbol(word);
        }
        return newSignature;
    }
}
