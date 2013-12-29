package org.getalp.lexsema.ontology;

/**
 *
 */
public class DefaultClassURICollection implements ClassURICollection {
    private OntologyModel model;
    private String prefix;
    private String baseURI;

    /*
     * Default URI value, indicates it must be retrieved first in getBaseURI()
     */ {
        baseURI = "";
    }

    public DefaultClassURICollection(OntologyModel model, String prefix) {
        this.model = model;
        this.prefix = prefix;
    }

    @Override
    public String getClassURI(String className) {
        return getBaseURI() + "#" + className;
    }

    @Override
    public String getBaseURI() {
        if (baseURI.isEmpty()) {
            baseURI = model.getUri(prefix + ":").getURI();
        }
        return baseURI;
    }

}
