package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.Language;


public abstract class SignatureEnrichmentAbstract implements SignatureEnrichment {

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, Language language) {
        return enrichSemanticSignature(semanticSignature);
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, String id) {
        return enrichSemanticSignature(semanticSignature);
    }

    public void close() {
        
    }
}
