package org.getalp.lexsema.similarity;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.Language;

import java.util.Collections;
import java.util.Map;


final class NullSense implements Sense {

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void setId(String id) {
    }

    @Override
    public Map<String, SemanticSignature> getRelatedSignatures() {
        return Collections.emptyMap();
    }

    @Override
    public SemanticSignature getSemanticSignature() {
        return DefaultSemanticSignatureFactory.DEFAULT.createNullSemanticSignature();
    }

    @Override
    public void setSemanticSignature(SemanticSignature semanticSignature) {
    }

    @Override
    public void addRelatedSignature(String key, SemanticSignature semanticSignature) {
    }

    @Override
    public double computeSimilarityWith(SimilarityMeasure measure, Sense other) {
        return 0;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public Language getLanguage() {
        return Language.UNSUPPORTED;
    }

}
