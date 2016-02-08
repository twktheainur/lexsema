package org.getalp.lexsema.similarity.measures.lesk;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import java.util.Map;

public class IndexedDiceLeskSimilarity extends IndexedLeskSimilarity
{
    public double compute(SemanticSignature sigA, SemanticSignature sigB)
    {
        double score = super.compute(sigA, sigB);
        score = (2.0 * score) / (((double) sigA.size()) + ((double) sigB.size()));
        return score;
    }

    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB)
    {
    	return compute(sigA, sigB);
    }
}
