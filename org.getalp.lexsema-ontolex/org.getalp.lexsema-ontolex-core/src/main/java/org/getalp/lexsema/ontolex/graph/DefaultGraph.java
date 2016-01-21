/**
 *
 */
package org.getalp.lexsema.ontolex.graph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import org.getalp.lexsema.ontolex.Graph;


/**
 * The default Graph interface implementation
 */
public class DefaultGraph implements Graph {
    private String nodeUri;
    private final OntologyModel model;

    public DefaultGraph(String nodeUri, OntologyModel model) {
        this.nodeUri = nodeUri;
        this.model = model;
    }

    @Override
    public OntologyModel getModel() {
        return model;
    }

    @Override
    public Node getJenaNode() {
        return NodeFactory.createURI(nodeUri);
    }
}
