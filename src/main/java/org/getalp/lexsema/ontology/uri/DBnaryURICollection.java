package org.getalp.lexsema.ontology.uri;

import org.getalp.lexsema.ontology.OntologyModel;

/**
 * An URI collection for lemon classes that allows to retrieve full URIs from prefixed forms
 */
public class DBNaryURICollection extends AbstractURICollection {
    public DBNaryURICollection(OntologyModel model) {
        super(model, "dbnary");
    }
}
