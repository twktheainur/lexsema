package org.getalp.lexsema.ontology;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;

/**
 * Interface to the Jena OntModel Wrapper
 */
public interface OntologyModel {

    OntModel getJenaModel();

    Node getNode(String element);
}
