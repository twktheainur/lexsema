package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.Language;


public interface SignatureEnrichment {

    SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature);

    SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, Language language);

    void close();
}
