package org.getalp.lexsema.similarity;

import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.io.Serializable;
import java.util.Map;

/**
 * The representation of a LexicalSense in a text annotation setting
 */
public interface Sense extends LexicalSense, Serializable {
    String getId();
    
    void setId(String id);

    Map<String, SemanticSignature> getRelatedSignatures();

    SemanticSignature getSemanticSignature();

    void setSemanticSignature(SemanticSignature semanticSignature);

    void setLexicalSense(LexicalSense lexicalSense);

    void addRelatedSignature(String key, SemanticSignature semanticSignature);

    double computeSimilarityWith(SimilarityMeasure measure, Sense other);
}
