package org.getalp.lexsema.lexicalresource;

import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.graph.Node;

/**
 * A node that is part of a Lexical Resource, i.e. corresponding to an instance of one of the Lemon classes
 */
public interface LexicalResourceEntity extends Node {
    /**
     * @return The lexical resource the Entity belongs to
     */
    public LexicalResource getLexicalResource();

    /**
     * Returns the ontology model associated with the lexical resource and the entity
     *
     * @return the ontology model associated with the lexical resource and the entity
     */
    public OntologyModel getOntologyModel();
}
