package org.getalp.lexsema.similarity.measures.lesk;

import ac.similarity.ExtendedLeskSenseSimilarity;
import ac.similarity.dictionary.Definition;
import ac.similarity.input.DefinitionInput;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.util.Map;

public class ACExtendedLeskSimilarity implements SimilarityMeasure {

    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB, Map<String, SemanticSignature> relatedSignaturesA, Map<String, SemanticSignature> relatedSignaturesB) {
        Definition defA = new Definition(sigA.toString());
        Definition defB = new Definition(sigB.toString());
        DefinitionInput di = new DefinitionInput(defA, defB, null);
        return new ExtendedLeskSenseSimilarity().computeSimilarity(di, false);
    }

    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB) {
        return compute(sigA,sigB,null,null);
    }
}
