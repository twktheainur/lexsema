package org.getalp.lexsema.ontolex;

import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.graph.defaultimpl.DefaultGraph;
import org.getalp.lexsema.ontolex.graph.queries.TripleFactory;


public abstract class AbstractLexicalResource implements LexicalResource {

    private Graph graph;
    private OntologyModel model;
    private TripleFactory tripleFactory;


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


    protected OntologyModel getModel() {
        return model;
    }


    protected TripleFactory getTripleFactory() {
        return tripleFactory;
    }
}
