package org.getalp.lexsema.similarity.measures.lesk;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.util.List;
import java.util.Map;

public class AnotherLeskSimilarity implements SimilarityMeasure
{
    public boolean normalize;

    public AnotherLeskSimilarity() {
        normalize=false;
    }

    public AnotherLeskSimilarity(boolean normalize) {
        this.normalize = normalize;
    }

    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB)
    {
        List<String> la = sigA.getStringSymbols();
        List<String> lb = sigB.getStringSymbols();
        int aSize = la.size();
        int bSize = lb.size();
        int count = 0;
        for (String aLa : la) {
            for (String aLb : lb) {
                if (aLa.equals(aLb)) {
                    count++;
                }
            }
        }
        if (normalize) {
            return count / ((double) aSize * bSize);
        } else {
            return count;
        }
    }
    


    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB)
    {
    	return compute(sigA, sigB);
    }
}
