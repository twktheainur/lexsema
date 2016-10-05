package org.getalp.lexsema.similarity.measures.lesk;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import java.util.List;
import java.util.Map;

public class SimpleLeskSimilarity implements SimilarityMeasure
{
    public double compute(SemanticSignature sigA, SemanticSignature sigB)
    {
        List<String> la = sigA.getStringSymbols();
        List<String> lb = sigB.getStringSymbols();
        int count = 0;
        for (String ala : la)
        {
            for (String alb : lb)
            {
                if (ala.equals(alb))
                {
                    count++;
                }
            }
        }
        return count;
    }
    
    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB)
    {
    	return compute(sigA, sigB);
    }
}
