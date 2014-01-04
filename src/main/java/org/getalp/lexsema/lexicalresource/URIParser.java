package org.getalp.lexsema.lexicalresource;

import org.getalp.lexsema.ontology.graph.Node;

/**
 * Interface for URI parsers, whose aim is to extract information encoded in the URI
 */
public interface URIParser {
    /**
     * Parse the information contained in the URI of the Node and possibly set relevant values in the <code>Node</code> instance
     *
     * @param entry The Node instance
     */
    public void parseURI(Node entry);
}
