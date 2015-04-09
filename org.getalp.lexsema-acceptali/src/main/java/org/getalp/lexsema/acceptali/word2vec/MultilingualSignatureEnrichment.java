package org.getalp.lexsema.acceptali.word2vec;

import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.similarity.signatures.SignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;


public interface MultilingualSignatureEnrichment extends SignatureEnrichment {

    @Override
    StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature);

    StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature, Language language);
}
