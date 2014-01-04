/**
 *
 */
package org.getalp.lexsema.ontology.graph.defaultimpl;

import com.hp.hpl.jena.graph.NodeFactory;
import org.getalp.lexsema.lexicalresource.URIParser;
import org.getalp.lexsema.ontology.graph.Node;
import org.getalp.lexsema.ontology.graph.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * An anstract node in and ontology graph
 */
public abstract class AbstractNode implements Node {
    private com.hp.hpl.jena.graph.Node node;
    private List<Relation> propertyCache;

    /**
     * Constructor
     *
     * @param uri Uri of the node
     * @param p   uri parser that can potentially extract informatiion from the URI
     */
    protected AbstractNode(String uri, URIParser p) {
        node = NodeFactory.createURI(uri);
        if (p != null) {
            p.parseURI(this);
        }
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