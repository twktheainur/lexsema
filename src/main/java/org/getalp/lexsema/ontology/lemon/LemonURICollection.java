package org.getalp.lexsema.ontology.lemon;

import org.getalp.lexsema.ontology.DefaultClassURICollection;
import org.getalp.lexsema.ontology.OntologyModel;

/**
 * Created by tchechem on 27/12/13.
 */
public class LemonURICollection extends DefaultClassURICollection {
    private OntologyModel model;
    private String prefix;

    public LemonURICollection(OntologyModel model) {
        super(model, "lemon");
    }
}
