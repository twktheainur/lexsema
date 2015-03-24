package org.getalp.lexsema.acceptali.word2vec;


import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.SignatureEnrichment;

import java.util.HashMap;
import java.util.Map;

public class MultilingualWord2VecSignatureEnrichment implements MultilingualSignatureEnrichment {

    private Map<Language, SignatureEnrichment> processors;

    public MultilingualWord2VecSignatureEnrichment(MultilingualWord2VecLoader multilingualWord2VecLoader, int topN) {
        processors = new HashMap<>();
        for (Language language : multilingualWord2VecLoader.getLanguages()) {
            processors.put(language, new JedisCachedSignatureEnrichment(new Word2VecLocalSignatureEnrichment(multilingualWord2VecLoader.getWord2Vec(language), topN)));
        }
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        SemanticSignature finalSig = new SemanticSignatureImpl();
        for (Language language : processors.keySet()) {
            finalSig.appendSignature(processors.get(language).enrichSemanticSignature(semanticSignature));
        }
        return finalSig;
    }

    @Override
    public void close() {
        if (processors != null) {
            for (SignatureEnrichment se : processors.values()) {
                se.close();
            }
        }
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, Language language) {
        return processors.get(language).enrichSemanticSignature(semanticSignature);
    }
}
