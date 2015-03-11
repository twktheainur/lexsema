package org.getalp.lexsema.acceptali.word2vec;

import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SignatureEnrichment;

/**
 * Created by tchechem on 09/03/15.
 */
public interface MultilingualSignatureEnrichment extends SignatureEnrichment {

    @Override
    SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature);

    SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, Language language);
}
