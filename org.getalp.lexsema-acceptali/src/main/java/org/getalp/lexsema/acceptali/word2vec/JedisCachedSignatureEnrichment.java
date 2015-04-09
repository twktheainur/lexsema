package org.getalp.lexsema.acceptali.word2vec;

import org.getalp.lexsema.similarity.signatures.SignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.getalp.lexsema.util.caching.Cache;
import org.getalp.lexsema.util.caching.CachePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;


public class JedisCachedSignatureEnrichment implements SignatureEnrichment {

    private Logger logger = LoggerFactory.getLogger(JedisCachedSignatureEnrichment.class);

    private SignatureEnrichment signatureEnrichmentEngine;
    private String prefix;
    private Cache cache = CachePool.getResource();

    public JedisCachedSignatureEnrichment(String prefix, SignatureEnrichment signatureEnrichmentEngine) {
        this.prefix = prefix;
        this.signatureEnrichmentEngine = signatureEnrichmentEngine;

    }

    public JedisCachedSignatureEnrichment(String prefix) {
        this(prefix, null);

    }

    public JedisCachedSignatureEnrichment(SignatureEnrichment signatureEnrichmentEngine) {
        this("", signatureEnrichmentEngine);
    }

    private String produceKey(String key) {
        return String.format("%s____%s____%s", prefix, getClass().getCanonicalName(), key);
    }

    private StringSemanticSignature signatureFromCachedString(String cachedString) {
        StringSemanticSignature semanticSignature = new StringSemanticSignatureImpl();

        semanticSignature.addSymbolString(new ArrayList<>(Arrays.asList(cachedString.split(" "))));
        return semanticSignature;
    }

    @Override
    public void close() {
        cache.close();
    }

    @Override
    public StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature) {
        String key = produceKey(semanticSignature.toString());
        StringSemanticSignature signature;
        if (!cache.exists(key)) {
            if (signatureEnrichmentEngine != null) {
                signatureEnrichmentEngine.enrichSemanticSignature(semanticSignature);
                cache.set(key, semanticSignature.toString());
            }
            signature = semanticSignature;

        } else {
            signature = signatureFromCachedString(cache.get(key));
        }
        logger.info(semanticSignature.toString());
        return signature;
    }
}
