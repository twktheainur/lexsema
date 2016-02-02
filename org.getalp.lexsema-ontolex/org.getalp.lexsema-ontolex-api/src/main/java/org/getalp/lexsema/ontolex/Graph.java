package org.getalp.lexsema.ontolex;

import org.getalp.lexsema.ontolex.graph.OntologyModel;

import java.io.Serializable;

/**
 * A wrapper interface for the Jena Graph API
 */
public interface Graph extends Serializable{
    /**
     * Get the associated {@code OntologyModel}
     *
     * @return The {@code Ontology} model
     */
    public OntologyModel getModel();

    /**
     * Return the corresponding Jena Node
     *
     * @return The corresponding Jena node
     */
    public com.hp.hpl.jena.graph.Node getJenaNode();

}
