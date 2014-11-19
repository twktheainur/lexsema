package org.getalp.lexsema.ontolex.graph;

/**
 * A wrapper interface for the Jena Graph API
 */
public interface Graph {
    /**
     * Get the associated <code>OntologyModel</code>
     *
     * @return The <code>Ontology</code> model
     */
    public OntologyModel getModel();

    /**
     * Return the corresponding Jena Node
     *
     * @return The corresponding Jena node
     */
    public com.hp.hpl.jena.graph.Node getJenaNode();

}
