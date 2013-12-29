package org.getalp.lexsema.lexicalresource;

import org.getalp.lexsema.ontology.ClassURICollection;
import org.getalp.lexsema.ontology.graph.Graph;
import org.getalp.lexsema.ontology.graph.Node;

/**
 * Generic Interface for LexicalResources
 */
public interface LexicalResource {
    String getURI();

    Graph getGraph();

    void parseURI(Node n);

    public ClassURICollection getURICOllection();
}
