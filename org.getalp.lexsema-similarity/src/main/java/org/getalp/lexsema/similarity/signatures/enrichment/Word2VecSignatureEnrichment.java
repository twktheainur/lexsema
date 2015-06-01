package org.getalp.lexsema.similarity.signatures.enrichment;


import org.deeplearning4j.models.word2vec.Word2Vec;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;

import java.util.HashMap;
import java.util.Map;

public class Word2VecSignatureEnrichment implements SignatureEnrichment {

    private Map<Language, SignatureEnrichment> processors;

    public Word2VecSignatureEnrichment(Map<Language, Word2Vec> multilingualWord2VecLoader, int topN) {
        processors = new HashMap<>();
        if (multilingualWord2VecLoader != null) {
            for (Language language : multilingualWord2VecLoader.keySet()) {
                processors.put(language, new JedisCachedSignatureEnrichment(
                        String.format("es%d", topN),
                        new Word2VecLocalSignatureEnrichment(multilingualWord2VecLoader.get(language), topN)));
            }
        }
        processors.put(Language.UNSUPPORTED, new JedisCachedSignatureEnrichment(
                String.format("es%d", topN)));
    }

    @Override
    public StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature) {
        StringSemanticSignature finalSig = new StringSemanticSignatureImpl();
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
    public StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature, Language language) {
        SignatureEnrichment processor;
        if (processors.containsKey(language)) {
            processor = processors.get(language);
        } else {
            processor = processors.get(Language.UNSUPPORTED);
        }
        return processor.enrichSemanticSignature(semanticSignature);
    }
}
