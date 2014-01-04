package org.getalp.lexsema.ontology.uri;

/**
 * Allows to retrieve full owl class URIs for a given prefix
 */
public interface URICollection {
    String forName(String className);

    /**
     * Returns the base URI corresponding to the prefix
     *
     * @return the base URI corresponding to the the prefix
     */
    String getBaseURI();
}
