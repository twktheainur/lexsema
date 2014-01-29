package org.getalp.lexsema.ontology.uri;

import org.getalp.lexsema.ontology.OntologyModel;

/**
 * An URI collection for lemon classes that allows to retrieve full URIs from prefixed forms
 */
public class DBnaryURICollection extends AbstractURICollection {
    public DBnaryURICollection(OntologyModel model) {
        super(model, "dbnary");
    }
}
