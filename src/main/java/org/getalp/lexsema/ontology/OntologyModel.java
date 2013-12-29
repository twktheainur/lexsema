package org.getalp.lexsema.ontology;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;

import java.io.IOException;

/**
 * Interface to the Jena OntModel Wrapper
 */
public interface OntologyModel {
    void loadProperties() throws IOException;

    OntModel getJenaModel();

    Node getUri(String element);
}
