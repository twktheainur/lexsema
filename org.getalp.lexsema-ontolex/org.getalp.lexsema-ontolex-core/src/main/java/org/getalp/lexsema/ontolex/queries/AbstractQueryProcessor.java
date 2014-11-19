package org.getalp.lexsema.ontolex.queries;


import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.expr.Expr;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.graph.OntologyModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Common methods for <code>QueryProcessor</code> implementations.
 *
 * @param <T> The type of <code>QueryProcessor</code> implemented
 */
public abstract class AbstractQueryProcessor<T> implements QueryProcessor<T> {
    private OntologyModel model;
    private Graph graph;
    private ARQQuery query;
    private ResultSet results;
    private Collection<Triple> triples = new ArrayList<>();
    private Collection<String> resultVars = new ArrayList<>();
    private Collection<Expr> filters = new ArrayList<>();

    protected AbstractQueryProcessor(Graph graph) {
        this.graph = graph;
        model = graph.getModel();
    }

    @Override
    public void runQuery() {
        results = query.runQuery();
    }


    protected Node getNode(String uri) {
        return model.getNode(uri);
    }

    protected void initialize() {
        defineQuery();
        query.initialize(graph, triples, resultVars, filters);
    }

    protected void addTriple(Node a, Node b, Node c) {
        triples.add(Triple.create(a, b, c));
    }

    protected void addFilter(Expr e) {
        filters.add(e);
    }

    protected void addResultVar(String var) {
        resultVars.add(var);
    }

    protected boolean hasNextResult() {
        return results.hasNext();
    }

    protected QuerySolution nextSolution() {
        return results.nextSolution();
    }

    protected abstract void defineQuery();

    @Override
    public ARQQuery getQuery() {
        return query;
    }

    protected void setQuery(ARQQuery query) {
        this.query = query;
    }

}
