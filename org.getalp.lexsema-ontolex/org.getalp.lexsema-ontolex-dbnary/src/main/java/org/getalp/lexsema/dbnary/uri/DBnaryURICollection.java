package org.getalp.lexsema.dbnary.uri;


import org.getalp.ontolex.api.graph.OntologyModel;
import org.getalp.ontolexapi.core.uri.AbstractURICollection;

/**
 * An URI collection for ontolex classes that allows to retrieve full URIs from prefixed forms
 */
public class DBnaryURICollection extends AbstractURICollection {
    public DBnaryURICollection(OntologyModel model) {
        super(model, "dbnary");
    }
}
