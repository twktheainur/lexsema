package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.util.Language;


public interface SignatureEnrichment {

    StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature);

    StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature, Language language);

    public void close();
}
