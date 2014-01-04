package org.getalp.lexsema.ontology.graph;

import org.getalp.lexsema.ontology.OntologyModel;

public interface Graph {
    public OntologyModel getModel();

    public com.hp.hpl.jena.graph.Node getUri();
}
