package org.getalp.lexsema.similarity.signatures.enrichment;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.util.caching.Cache;
import org.getalp.lexsema.util.caching.CachePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;


public class CachedSignatureEnrichment extends SignatureEnrichmentAbstract {

    private final Logger logger = LoggerFactory.getLogger(CachedSignatureEnrichment.class);

    private final SignatureEnrichment signatureEnrichmentEngine;
    private final String prefix;
    private final Cache cache = CachePool.getResource();

    public CachedSignatureEnrichment(String prefix, SignatureEnrichment signatureEnrichmentEngine) {
        this.prefix = prefix;
        this.signatureEnrichmentEngine = signatureEnrichmentEngine;

    }

    public CachedSignatureEnrichment(String prefix) {
        this(prefix, null);

    }

    private String produceKey(String key) {
        final Class<? extends CachedSignatureEnrichment> aClass = getClass();
        return String.format("%s____%s____%s", prefix, aClass.getCanonicalName(), key);
    }

    private SemanticSignature signatureFromCachedString(String cachedString) {
        SemanticSignature semanticSignature = new SemanticSignatureImpl();

        semanticSignature.addSymbolString(new ArrayList<>(Arrays.asList(cachedString.split(" "))));
        return semanticSignature;
    }

    @Override
    public void close() {
        cache.close();
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        String key = produceKey(semanticSignature.toString());
        SemanticSignature signature;
        if (cache.exists(key)) {
            signature = signatureFromCachedString(cache.get(key));
        } else {
            if (signatureEnrichmentEngine != null) {
                signatureEnrichmentEngine.enrichSemanticSignature(semanticSignature);
                cache.set(key, semanticSignature.toString());
            }
            signature = semanticSignature;

        }
        logger.info(semanticSignature.toString());
        return signature;
    }

}
