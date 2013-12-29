package org.getalp.lexsema.lexicalresource;

import org.getalp.lexsema.ontology.graph.Node;

/**
 * Interface for URI parsers, whose aim is to extract information encoded in the URI
 */
public interface URIParser {
    public void extractInformation(Node entry);
}
