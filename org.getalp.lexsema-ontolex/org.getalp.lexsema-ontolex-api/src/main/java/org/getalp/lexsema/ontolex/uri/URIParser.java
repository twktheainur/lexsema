package org.getalp.lexsema.ontolex.uri;


import java.util.Map;

/**
 * Interface for URI parsers, whose aim is to extract information encoded in the URI of <code>LexicalResourceEntity</code>(ies).
 */
public interface URIParser {
    /**
     * Parse the information contained in a LexicalResourceEntity URI and return the resulting key/value map
     *
     * @param uri The URI to parse
     */
    public Map<String, String> parseURI(String uri);
}
