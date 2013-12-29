/**
 *
 */
package org.getalp.lexsema.ontology.graph.defaultimpl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.graph.Graph;
import org.getalp.lexsema.ontology.lemon.LexicalEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tchechem
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
    public List<LexicalEntry> getEntry(String lemma) {
        List<LexicalEntry> le = new ArrayList<>();
        return le;
    }

    @Override
    public Node getUri() {
        return uri;
    }

}
