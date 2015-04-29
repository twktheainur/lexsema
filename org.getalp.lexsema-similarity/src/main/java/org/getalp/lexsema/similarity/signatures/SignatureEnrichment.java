package org.getalp.lexsema.similarity.signatures;


public interface SignatureEnrichment {
    public StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature);
    public void close();
}
