package org.getalp.lexsema.similarity.measures.lesk;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.util.List;
import java.util.Map;

/**
 * A Simple Lesk Similarity, which manages String semantic signatures 
 * as well as Indexed semantic signatures.
 */
public class AnotherLeskSimilarity implements SimilarityMeasure
{
    public double compute(SemanticSignature sigA, SemanticSignature sigB)
    {
        if (sigA instanceof IndexedSemanticSignature && sigB instanceof IndexedSemanticSignature)
        {
            return computeIndexed((IndexedSemanticSignature) sigA, (IndexedSemanticSignature) sigB);
        }
        else
        {
            return computeString(sigA, sigB);
        }
    }
    
    public double computeString(SemanticSignature sigA, SemanticSignature sigB)
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
    
    public double computeIndexed(IndexedSemanticSignature sigA, IndexedSemanticSignature sigB)
    {
        List<Integer> la = sigA.getIndexedSymbols();
        List<Integer> lb = sigB.getIndexedSymbols(); 
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
