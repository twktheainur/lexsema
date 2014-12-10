package org.getalp.lexsema.similarity;

import com.hp.hpl.jena.graph.Node;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;

import java.util.Map;

/**
 * The representation of a LexicalSense in a text annotation setting
 */
public interface Sense extends LexicalSense {
    String getId();

    Map<String, SemanticSignature> getRelatedSignatures();

    @Override
    String getDefinition();

    @Override
    void setDefinition(String definition);

    @Override
    String getSenseNumber();

    @Override
    void setSenseNumber(String i);

    @Override
    LexicalResource getLexicalResource();

    @Override
    OntologyModel getOntologyModel();

    @Override
    Node getNode();

    public SemanticSignature getSemanticSignature();

    public void setSemanticSignature(SemanticSignature semanticSignature);

    public void setLexicalSense(LexicalSense lexicalSense);

    public void addRelatedSignature(String key, SemanticSignature semanticSignature);
}
