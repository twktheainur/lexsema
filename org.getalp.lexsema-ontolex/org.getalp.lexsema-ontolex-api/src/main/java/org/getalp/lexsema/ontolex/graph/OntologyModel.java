package org.getalp.lexsema.ontolex.graph;

/**
 * Interface to the Jena OntModel Wrapper
 */
public interface OntologyModel {


    /**
     * The graph node that corresponds to the <code>uri</code>.
     * This method expands prefixes that are loaded into the model
     *
     * @param uri The uri of the node.
     */
    com.hp.hpl.jena.graph.Node getNode(String uri);

}
