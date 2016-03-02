package org.getalp.lexsema.similarity.measures.lesk;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.VectorOperation;

import java.util.List;
import java.util.Map;

public class VectorizedLeskSimilarity implements SimilarityMeasure
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
                double[] alav = VectorOperation.to_vector(ala);
                double[] albv = VectorOperation.to_vector(alb);
                count += VectorOperation.dot_product(alav, albv);
            }
        }
        return count;
    }
    
    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB)
    {
    	return compute(sigA, sigB);
    }
}
