/**
 *
 */
package org.getalp.lexsema.ontolex.graph.defaultimpl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.graph.OntologyModel;


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
