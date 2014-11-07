package org.getalp.lexsema.ontolex.graph;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * Interface to the Jena OntModel Wrapper
 */
public interface OntologyModel {

    OntModel getJenaModel();

    Node getNode(String element);
}
