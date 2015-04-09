package org.getalp.lexsema.similarity.signatures;


import org.getalp.lexsema.similarity.measures.SimilarityMeasure;

import java.util.List;
import java.util.Map;

/**
 * Represent the semantic signature of a semantic unit of meaning (e.g. LexicalSense)
 */
public interface SemanticSignature {
    public double computeSimilarityWith(SimilarityMeasure measure, SemanticSignature other,
                                        Map<String, ? extends SemanticSignature> relatedA,
                                        Map<String, ? extends SemanticSignature> relatedB
    );
    public List<Double> getWeights();
    public int size();
}
