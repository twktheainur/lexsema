package org.getalp.lexsema.acceptali.word2vec;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.SignatureEnrichment;
import org.getalp.lexsema.util.JedisCachePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;


public class JedisCachedSignatureEnrichment implements SignatureEnrichment {

    private Logger logger = LoggerFactory.getLogger(JedisCachedSignatureEnrichment.class);

    private SignatureEnrichment signatureEnrichmentEngine;
    private String prefix;
    private Jedis jedis = JedisCachePool.getResource();

    public JedisCachedSignatureEnrichment(String prefix, SignatureEnrichment signatureEnrichmentEngine) {
        this.prefix = prefix;
        this.signatureEnrichmentEngine = signatureEnrichmentEngine;

    }

    public JedisCachedSignatureEnrichment(SignatureEnrichment signatureEnrichmentEngine) {
        this("", signatureEnrichmentEngine);

    }

    private String produceKey(String key) {
        return String.format("%s____%s____%s", prefix, getClass().getCanonicalName(), key);
    }

    private SemanticSignature signatureFromCachedString(String cachedString) {
        SemanticSignature semanticSignature = new SemanticSignatureImpl();
        semanticSignature.addSymbolString(cachedString);
        return semanticSignature;
    }

    @Override
    public void close() {
        jedis.close();
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        String key = produceKey(semanticSignature.toString());
        String extension;
        SemanticSignature signature;
        if (!jedis.exists(key)) {
            signatureEnrichmentEngine.enrichSemanticSignature(semanticSignature);
            signature = semanticSignature;
            jedis.set(key, semanticSignature.toString());
        } else {
            signature = signatureFromCachedString(jedis.get(key));
        }
        return signature;
    }
}
