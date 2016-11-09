package org.getalp.lexsema.similarity;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.Language;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


class SenseImpl implements Sense {
    private SemanticSignature semanticSignature;
    private String id = "";
    private final Map<String, SemanticSignature> relatedSignatures;
    private final Language language;

    SenseImpl(String id) {
        this(id, Language.NONE);
    }

    SenseImpl(String id, Language language) {
        this.id = id;
        relatedSignatures = new HashMap<>();
        semanticSignature = DefaultSemanticSignatureFactory.DEFAULT.createNullSemanticSignature();
        this.language = language;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Map<String, SemanticSignature> getRelatedSignatures() {
        return Collections.unmodifiableMap(relatedSignatures);
    }

    @Override
    public String toString() {
        return String.format("Sense|%s|{'%s'}", id, semanticSignature.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sense)) {
            return false;
        }
        Sense sense = (Sense) obj;
        return this == obj || id.equals(sense.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @Override
    public Language getLanguage() {
        return language;
    }


    @Override
    public SemanticSignature getSemanticSignature() {
        return semanticSignature;
    }

    @Override
    public void setSemanticSignature(SemanticSignature semanticSignature) {
        this.semanticSignature = semanticSignature;
        semanticSignature.setLanguage(getLanguage());
    }

    @Override
    public void addRelatedSignature(String key, SemanticSignature semanticSignature) {
        relatedSignatures.put(key, semanticSignature);
    }

    @Override
    public double computeSimilarityWith(SimilarityMeasure measure, Sense other) {
        return semanticSignature.computeSimilarityWith(measure, other.getSemanticSignature(),
                getRelatedSignatures(), other.getRelatedSignatures());
    }

    @Override
    public boolean isNull() {
        return false;
    }

}
