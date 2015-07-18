package org.getalp.lexsema.similarity.signatures;


public interface SignatureEnrichment {
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature);
    public void close();
}
