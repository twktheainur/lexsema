package org.getalp.lexsema.ontology.uri;

import com.hp.hpl.jena.rdf.model.Resource;
import org.getalp.lexsema.ontology.OntologyModel;

/**
 *
 */
public abstract class AbstractURICollection implements URICollection {
    private OntologyModel model;
    private String prefix;
    private String baseURI;

    /*
     * Default URI value, indicates it must be retrieved first in getBaseURI()
     */ {
        baseURI = "";
    }

    protected AbstractURICollection(OntologyModel model, String prefix) {
        this.model = model;
        this.prefix = prefix;
    }

    @Override
    public String forName(String className) {
        Resource r = model.getJenaModel().getResource(baseURI + className);
        if (r == null) {
            return null;
        }
        return getBaseURI() + className;
    }

    @Override
    public String getBaseURI() {
        if (baseURI.isEmpty()) {
            baseURI = model.getNode(prefix + ":").getURI();
        }
        return baseURI;
    }

}
