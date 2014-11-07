package org.getalp.lexsema.ontolex.uri;


import org.getalp.lexsema.ontolex.graph.OntologyModel;

/**
 * An URI collection for ontolex classes that allows to retrieve full URIs from prefixed forms
 */
public class OntolexURICollection extends AbstractURICollection {
    public OntolexURICollection(OntologyModel model) {
        super(model, "ontolex");
    }
}
