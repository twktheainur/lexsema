package org.getalp.lexsema.similarity.measures;

import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;

import java.util.List;
import java.util.Map;

/**
 * An overlap measure based on sorted and indexed definition bags of words. This is the lesk implementation from the formica
 * project.
 */
public class IndexedOverlapSimilarity implements SimilarityMeasure {


    private boolean normalize;

    @Override
    public double compute(StringSemanticSignature sigA, StringSemanticSignature sigB, Map<String, StringSemanticSignature> relatedSignaturesA, Map<String, StringSemanticSignature> relatedSignaturesB) {
        return 0;
    }

    @Override
    public double compute(IndexedSemanticSignature sigA, IndexedSemanticSignature sigB, Map<String, IndexedSemanticSignature> relatedSignaturesA, Map<String, IndexedSemanticSignature> relatedSignaturesB) {
        List<Integer> la = sigA.getSymbols();
        List<Integer> lb = sigB.getSymbols();
        int aSize = la.size();
        int bSize = lb.size();
        int count = 0;
        int i = 0;
        int j = 0;
        while (i < aSize && j < bSize) {
            if (la.get(i).compareTo(lb.get(j)) == 0 && !lb.get(j).equals(-1)) {
                count++;
                i++;
                j++;
            } else if (la.get(i).compareTo(lb.get(j)) == -1) {
                i++;
            } else {
                j++;
            }
        }
        if (normalize) {
            return (double) count / Math.min(aSize, bSize);
        }
        return count;
    }

    private SemanticSymbol getSignatureSymbolAt(int index, IndexedSemanticSignature signature) {
        return signature.getSymbol(index);
    }

    @SuppressWarnings({"MethodReturnOfConcreteClass", "BooleanParameter", "PublicMethodNotExposedInInterface"})
    public IndexedOverlapSimilarity normalize(boolean value) {
        normalize = value;
        return this;
    }
}
