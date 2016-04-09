package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.Language;

import java.io.Serializable;


public abstract class SignatureEnrichment implements Serializable {

    public abstract SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature);

    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, Language language) {
        return enrichSemanticSignature(semanticSignature);
    }

    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, String id) {
        return enrichSemanticSignature(semanticSignature);
    }

    void close() {
        
    }
}
