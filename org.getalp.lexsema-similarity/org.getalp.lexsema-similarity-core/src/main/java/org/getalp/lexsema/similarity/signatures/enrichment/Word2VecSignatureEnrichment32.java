package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.VectorOperation;
import org.getalp.lexsema.util.word2vec.Word2VecClient;

import java.util.Collection;

public class Word2VecSignatureEnrichment32 extends SignatureEnrichmentAbstract {

    private final int topN;
    
    private final double threshold;
    
    public Word2VecSignatureEnrichment32(int topN, double threshold) {
        this.topN = topN;
        this.threshold = threshold;
    }
    
    private double[] constructSenseVector(SemanticSignature semanticSignature, String id) {
    	double[] id_vector = Word2VecClient.getWordVector(id.substring(0, id.indexOf('%')));
    	if (id_vector.length == 0) return null;
    	double[] ret = null;
        for (SemanticSymbol semanticSymbol : semanticSignature) {
            double[] symbolVector = Word2VecClient.getWordVector(semanticSymbol.getSymbol());
            if (symbolVector.length == 0) continue;
            if (VectorOperation.dot_product(id_vector, symbolVector) < threshold) continue;
            if (ret == null) ret = symbolVector;
            else ret = VectorOperation.add(ret, symbolVector);
        }
        if (ret != null) ret = VectorOperation.normalize(ret);
        return ret;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, String id) {
    	double[] senseVector = constructSenseVector(semanticSignature, id);
    	if (senseVector == null) return semanticSignature;
        Collection<String> nearests = Word2VecClient.getMostSimilarWords(senseVector, topN);
        SemanticSignature newSignature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
        for (String word : semanticSignature.getStringSymbols()) {
            newSignature.addSymbol(word);
        }
        for (String word : nearests) {
            newSignature.addSymbol(word);
        }
        return newSignature;
    }
    
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
		return semanticSignature;
    }
}
