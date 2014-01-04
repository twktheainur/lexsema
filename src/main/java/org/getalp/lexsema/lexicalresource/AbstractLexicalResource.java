package org.getalp.lexsema.lexicalresource;

import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.graph.Graph;
import org.getalp.lexsema.ontology.graph.Node;
import org.getalp.lexsema.ontology.graph.defaultimpl.DefaultGraph;
import org.getalp.lexsema.ontology.graph.queries.TripleFactory;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractLexicalResource implements LexicalResource {

    private Map<Class<? extends Node>, URIParser> uriParsers;
    private Graph graph;
    private OntologyModel model;
    private TripleFactory tripleFactory;

    {
        uriParsers = new HashMap<>();
    }

    protected AbstractLexicalResource(OntologyModel model) {
        this.model = model;
        tripleFactory = new TripleFactory(model);
    }

    @Override
    public Graph getGraph() {
        if (graph == null) {
            graph = new DefaultGraph(getURI(), model);
        }
        return graph;
    }

    protected void addParser(Class<? extends Node> cn, URIParser up) {
        uriParsers.put(cn, up);
    }

    protected OntologyModel getModel() {
        return model;
    }

    @Override
    public void parseURI(Node n) {
        if (uriParsers.containsKey(n.getClass())) {
            URIParser parser = uriParsers.get(n.getClass());
            parser.parseURI(n);
        }
    }

    protected TripleFactory getTripleFactory() {
        return tripleFactory;
    }
}
