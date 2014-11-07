package org.getalp.lexsema.ontolex;


import org.getalp.lexsema.ontolex.graph.Node;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

/**
 * A node that is part of a Lexical Resource, i.e. corresponding to an instance of one of the Lemon classes
 */
public interface LexicalResourceEntity extends Node {
    /**
     * @return The lexical resource the Entity belongs to
     */
    public LexicalResource getLexicalResource();

    /**
     * Returns the graphapi model associated with the lexical resource and the entity
     *
     * @return the graphapi model associated with the lexical resource and the entity
     */
    public OntologyModel getOntologyModel();
}
