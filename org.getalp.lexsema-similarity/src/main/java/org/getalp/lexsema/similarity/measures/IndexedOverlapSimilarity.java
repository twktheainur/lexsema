package org.getalp.lexsema.similarity.measures;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.util.Map;

/**
 * An overlap measure based on sorted and indexed definition bags of words. This is the lesk implementation from the formica
 * project.
 */
public class IndexedOverlapSimilarity implements SimilarityMeasure {
    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB) {
        int aSize = sigA.size();
        int bSize = sigB.size();
        int count = 0;
        int i = 0;
        int j = 0;
        while (i < aSize && j < bSize) {
            if (getSignatureSymbolAt(i, sigA).equals(getSignatureSymbolAt(j, sigB)) && !getSignatureSymbolAt(j, sigB).equals("-1")) {
                count++;
                i++;
                j++;
            } else if (getSignatureSymbolAt(i, sigA).compareTo(getSignatureSymbolAt(j, sigB)) == -1) {
                i++;
            } else {
                j++;
            }
        }
        return count;
    }

    private String getSignatureSymbolAt(int index, SemanticSignature signature) {
        return signature.getSymbols().get(index);
    }
}
