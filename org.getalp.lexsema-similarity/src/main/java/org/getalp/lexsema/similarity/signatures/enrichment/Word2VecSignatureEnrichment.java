package org.getalp.lexsema.similarity.signatures.enrichment;


import org.deeplearning4j.models.word2vec.Word2Vec;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;

import java.util.HashMap;
import java.util.Map;

public class Word2VecSignatureEnrichment extends SignatureEnrichment {

    private final Map<Language, SignatureEnrichment> processors;

    public Word2VecSignatureEnrichment(Map<Language, Word2Vec> multilingualWord2VecLoader, int topN) {
        processors = new HashMap<>();
        if (multilingualWord2VecLoader != null) {
            for (Map.Entry<Language, Word2Vec> languageWord2VecEntry : multilingualWord2VecLoader.entrySet()) {
                processors.put(languageWord2VecEntry.getKey(), new JedisCachedSignatureEnrichment(
                        String.format("es%d", topN),
                        new Word2VecLocalSignatureEnrichment(languageWord2VecEntry.getValue(), topN)));
            }
        }
        processors.put(Language.UNSUPPORTED, new JedisCachedSignatureEnrichment(
                String.format("es%d", topN)));
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        SemanticSignature finalSig = new SemanticSignatureImpl();
        for (Map.Entry<Language, SignatureEnrichment> languageSignatureEnrichmentEntry : processors.entrySet()) {
            finalSig.appendSignature(languageSignatureEnrichmentEntry.getValue().enrichSemanticSignature(semanticSignature));
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
        SignatureEnrichment processor;
        if (processors.containsKey(language)) {
            processor = processors.get(language);
        } else {
            processor = processors.get(Language.UNSUPPORTED);
        }
        return processor.enrichSemanticSignature(semanticSignature);
    }
}
