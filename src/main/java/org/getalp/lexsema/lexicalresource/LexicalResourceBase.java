package org.getalp.lexsema.lexicalresource;

import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.graph.Graph;
import org.getalp.lexsema.ontology.graph.Node;
import org.getalp.lexsema.ontology.graph.defaultimpl.DefaultGraph;

import java.util.HashMap;
import java.util.Map;


public abstract class LexicalResourceBase implements LexicalResource {

    private Map<Class<? extends Node>, URIParser> uriParsers;
    private Graph graph;
    private OntologyModel model;

    {
        uriParsers = new HashMap<>();
    }

    protected LexicalResourceBase(OntologyModel model) {
        this.model = model;
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
        if (uriParsers.containsKey(n)) {
            URIParser parser = uriParsers.get(n.getClass());
            parser.extractInformation(n);
        }
    }

}
