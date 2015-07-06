package org.getalp.lexsema.similarity.measures.lesk;

import java.util.List;
import java.util.Map;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;

public class AnotherLeskSimilarity implements SimilarityMeasure
{
    public boolean normalize = false;
    
    public double compute(StringSemanticSignature sigA, StringSemanticSignature sigB)
    {
        List<String> la = sigA.getSymbols();
        List<String> lb = sigB.getSymbols();
        int aSize = la.size();
        int bSize = lb.size();
        int count = 0;
        for (int i = 0 ; i < aSize ; i++)
        {
        	for (int j = 0 ; j < bSize ; j++)
        	{
        		if (la.get(i).equals(lb.get(j)))
        		{
        			count++;
        		}
        	}
        }
        if (normalize) return (((double) count) / ((double) aSize * bSize));
        else return count;
    }

    public double compute(IndexedSemanticSignature sigA, IndexedSemanticSignature sigB)
    {
        List<Integer> la = sigA.getSymbols();
        List<Integer> lb = sigB.getSymbols();
        int aSize = la.size();
        int bSize = lb.size();
        int count = 0;
        for (int i = 0 ; i < aSize ; i++)
        {
        	for (int j = 0 ; j < bSize ; j++)
        	{
        		if (la.get(i).equals(lb.get(j)))
        		{
        			count++;
        		}
        	}
        }
        if (normalize) return (((double) count) / ((double) aSize * bSize));
        return count;
    }

    public double compute(StringSemanticSignature sigA, StringSemanticSignature sigB, Map<String, StringSemanticSignature> relatedSignaturesA, Map<String, StringSemanticSignature> relatedSignaturesB)
    {
    	return compute(sigA, sigB);
    }

    public double compute(IndexedSemanticSignature sigA, IndexedSemanticSignature sigB, Map<String, IndexedSemanticSignature> relatedSignaturesA, Map<String, IndexedSemanticSignature> relatedSignaturesB)
    {
    	return compute(sigA, sigB);
    }
}
