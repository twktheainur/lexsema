/**
 *
 */
package org.getalp.lexsema.ontolex.graph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;


/**
 * The default Graph interface implementation
 */
public class DefaultGraph implements Graph {
    private Node uri;
    private OntologyModel model;

    public DefaultGraph(String uri, OntologyModel model) {
        this.uri = NodeFactory.createURI(uri);
        this.model = model;
    }

    @Override
    public OntologyModel getModel() {
        return model;
    }

    @Override
    public Node getJenaNode() {
        return uri;
    }
}
