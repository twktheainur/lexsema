package org.getalp.lexsema.similarity.measures.lesk;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.VectorizedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.VectorizedSemanticSymbol;
import org.getalp.lexsema.util.VectorOperation;

import java.util.List;
import java.util.Map;

public class VectorizedLeskSimilarity implements SimilarityMeasure
{
    public double compute(SemanticSignature sigA, SemanticSignature sigB)
    {
        List<VectorizedSemanticSymbol> la = ((VectorizedSemanticSignature) sigA).getVectorizedSymbols();
        List<VectorizedSemanticSymbol> lb = ((VectorizedSemanticSignature) sigB).getVectorizedSymbols();
        double count = 0;
        for (VectorizedSemanticSymbol ala : la)
        {
            if (ala.getVector().length == 0) continue;
            for (VectorizedSemanticSymbol alb : lb)
            {
                if (alb.getVector().length == 0) continue;
                count += VectorOperation.dot_product(ala.getVector(), alb.getVector());
            }
        }
        return count;
    }
    
    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB)
    {
    	return compute(sigA, sigB);
    }
}
