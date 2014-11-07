/**
 *
 */
package org.getalp.lexsema.ontolex.graph.defaultimpl;

import com.hp.hpl.jena.graph.NodeFactory;
import lombok.EqualsAndHashCode;
import org.getalp.lexsema.ontolex.graph.Node;
import org.getalp.lexsema.ontolex.graph.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract node in and graphapi graph
 */
@EqualsAndHashCode
public abstract class AbstractNode implements Node {
    private com.hp.hpl.jena.graph.Node node;
    private List<Relation> propertyCache;

    /**
     * Constructor
     *
     * @param uri Uri of the node
     */
    protected AbstractNode(String uri) {
        node = NodeFactory.createURI(uri);
    }

    private void loadProperties() {
        if (propertyCache == null) {
            propertyCache = new ArrayList<>();
        }
        if (propertyCache.isEmpty()) {

        }
    }

    @Override
    public List<Relation> getRelated() {
        if (propertyCache == null) {
            loadProperties();
        }
        return propertyCache;
    }

    @Override
    public String getURI() {
        return node.toString();
    }
}