package org.getalp.lexsema.similarity.measures.lesk;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.util.List;
import java.util.Map;

public class IndexedLeskSimilarity implements SimilarityMeasure
{
    public double compute(SemanticSignature sigA, SemanticSignature sigB)
    {
        List<Integer> la = ((IndexedSemanticSignature) sigA).getIndexedSymbols();
        List<Integer> lb = ((IndexedSemanticSignature) sigB).getIndexedSymbols(); 
        int aSize = la.size();
        int bSize = lb.size();
        int count = 0;
        int i = 0;
        int j = 0;
        while (i < aSize && j < bSize)
        {
            int cmp = la.get(i).compareTo(lb.get(j));
            if (cmp == 0)
            {
                count++;
                i++;
                j++;
            }
            else if (cmp < 0) 
            {
                i++;
            } 
            else 
            {
                j++;
            }
        }
        return count;
    }

    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB)
    {
    	return compute(sigA, sigB);
    }
}
