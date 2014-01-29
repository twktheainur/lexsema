package org.getalp.lexsema.lexicalresource;

import org.getalp.lexsema.ontology.OntologyModel;
import org.getalp.lexsema.ontology.graph.Graph;
import org.getalp.lexsema.ontology.graph.defaultimpl.DefaultGraph;
import org.getalp.lexsema.ontology.graph.queries.TripleFactory;


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
