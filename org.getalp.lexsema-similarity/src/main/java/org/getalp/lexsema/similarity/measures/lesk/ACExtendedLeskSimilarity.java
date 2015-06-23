package org.getalp.lexsema.similarity.measures.lesk;

import ac.similarity.ExtendedLeskSenseSimilarity;
import ac.similarity.dictionary.Definition;
import ac.similarity.input.DefinitionInput;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;

import java.util.Map;

public class ACExtendedLeskSimilarity implements SimilarityMeasure {

    @Override
    public double compute(StringSemanticSignature sigA, StringSemanticSignature sigB, Map<String, StringSemanticSignature> relatedSignaturesA, Map<String, StringSemanticSignature> relatedSignaturesB) {
        Definition defA = new Definition(sigA.toString());
        Definition defB = new Definition(sigB.toString());
        DefinitionInput di = new DefinitionInput(defA, defB, null);
        return new ExtendedLeskSenseSimilarity().computeSimilarity(di, false);
    }

    @Override
    public double compute(StringSemanticSignature sigA, StringSemanticSignature sigB) {
        return compute(sigA,sigB,null,null);
    }
    @Override
    public double compute(IndexedSemanticSignature sigA, IndexedSemanticSignature sigB) {
        return compute(sigA,sigB,null, null);
    }

    @Override
    public double compute(IndexedSemanticSignature sigA, IndexedSemanticSignature sigB, Map<String, IndexedSemanticSignature> relatedSignaturesA, Map<String, IndexedSemanticSignature> relatedSignaturesB) {
        return 0;
    }
}
