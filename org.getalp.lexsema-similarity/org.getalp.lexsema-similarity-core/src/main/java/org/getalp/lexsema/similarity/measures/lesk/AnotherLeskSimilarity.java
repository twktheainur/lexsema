package org.getalp.lexsema.similarity.measures.lesk;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import java.util.Map;

public class AnotherLeskSimilarity implements SimilarityMeasure
{
    IndexedLeskSimilarity indexed = new IndexedLeskSimilarity();
    
    SimpleLeskSimilarity simple = new SimpleLeskSimilarity();
    
    public double compute(SemanticSignature sigA, SemanticSignature sigB)
    {
        if (sigA instanceof IndexedSemanticSignature && sigB instanceof IndexedSemanticSignature) {
            return indexed.compute(sigA, sigB);
        } else {
            return simple.compute(sigA, sigB);
        }
    }

    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB)
    {
    	return compute(sigA, sigB);
    }
}
