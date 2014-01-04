package org.getalp.lexsema.ontology.graph;

import java.util.List;

/**
 * Interface representing a Node of the resource
 */
public interface Node {

    /**
     * Get all relations having the node as a source
     *
     * @return list of all relations having the node as a source
     */
    List<Relation> getRelated();

    /**
     * Get the URI of the node
     *
     * @return the URI of the node
     */
    String getURI();
}
