package org.getalp.lexsema.ontolex.uri;


import org.getalp.lexsema.ontolex.graph.OntologyModel;

/**
 * Collection of RDF Classes URIs
 */

//TODO: Completely add all RDF classes
public class RDFURICollection extends AbstractURICollection {

    public RDFURICollection(OntologyModel model) {
        super(model, "rdf");
    }
}
