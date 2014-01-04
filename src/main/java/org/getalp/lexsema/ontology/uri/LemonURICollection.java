package org.getalp.lexsema.ontology.uri;

import org.getalp.lexsema.ontology.OntologyModel;

/**
 * An URI collection for lemon classes that allows to retrieve full URIs from prefixed forms
 */
public class LemonURICollection extends AbstractURICollection {
    public LemonURICollection(OntologyModel model) {
        super(model, "lemon");
    }
}
