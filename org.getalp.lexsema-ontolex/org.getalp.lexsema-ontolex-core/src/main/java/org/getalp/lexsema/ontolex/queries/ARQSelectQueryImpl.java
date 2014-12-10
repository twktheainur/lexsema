package org.getalp.lexsema.ontolex.queries;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for creating and running Select queries through Jena ARQ
 */
public class ARQSelectQueryImpl implements ARQSelectQuery {

    private Query query;
    private ElementTriplesBlock whereBlock;
    private ElementTriplesBlock optionalBlock;
    private List<Expr> filters;
    private Store store;

    /*
     * Internal Initializations
     */ {
        query = QueryFactory.create();
        whereBlock = new ElementTriplesBlock();
        optionalBlock = new ElementTriplesBlock();
        store = StoreHandler.getStore();
        filters = new ArrayList<>();
    }

    public ARQSelectQueryImpl() {
    }

    @Override
    public ResultSet runQuery() {
        return runQuery(0);
    }

    @Override
    public ResultSet runQuery(int limit) {
        ElementGroup eg = new ElementGroup();
        eg.addElement(whereBlock);
        eg.addElement(new ElementOptional(optionalBlock));
        for (Expr filter : filters) {
            eg.addElementFilter(new ElementFilter(filter));
        }
        query.setQueryPattern(eg);
        query.setQuerySelectType();
        if (limit > 0) {
            query.setLimit(limit);
        }
        return store.runQuery(query);
    }

    @Override
    public void addToWhereStatement(Triple t) {
        whereBlock.addTriple(t);
    }

    @Override
    public void addOptionalToWhereStatement(Triple t) {
        optionalBlock.addTriple(t);
    }

    @Override
    public void addResult(String var) {
        query.addResultVar(var);
    }

    @Override
    public void addFilter(Expr filter) {
        filters.add(filter);
    }

    @Override
    public void addToFromStatement(Graph g) {
        String uri = g.getJenaNode().toString();
        //query.addGraphURI(uri.substring(0,uri.length()-1));
        query.addGraphURI(uri);
    }

    /**
     * Set whether the annotresult n-uple should be distinct
     *
     * @param b when b is true, the DISTINCT keyword is added to the query.
     */
    @SuppressWarnings("all") /*Boolean parameter not recommended unless used in a setter*/
    @Override
    public void setDistinct(boolean b) {
        query.setDistinct(b);
    }


    @Override
    public ResultSet runQuery(Graph g, Iterable<Triple> triples, Iterable<Triple> optionalTriples, Iterable<String> resultVars) {
        initialize(g, triples, optionalTriples, resultVars);
        return runQuery();
    }

    @Override
    public void initialize(Graph g, Iterable<Triple> triples, Iterable<Triple> optionalTriples, Iterable<String> resultVars) {
        addToFromStatement(g);
        for (Triple t : triples) {
            addToWhereStatement(t);
        }
        if (optionalTriples != null) {
            for (Triple t : optionalTriples) {
                addOptionalToWhereStatement(t);
            }
        }
        for (String rv : resultVars) {
            addResult(rv);
        }
    }


    @Override
    public ResultSet runQuery(Graph g, Iterable<Triple> triples, Iterable<Triple> optionalTriples, Iterable<String> resultVars, Iterable<Expr> filters) {
        initialize(g, triples, optionalTriples, resultVars, filters);
        return runQuery();
    }

    @Override
    public void initialize(Graph g, Iterable<Triple> triples, Iterable<Triple> optionalTriples, Iterable<String> resultVars, Iterable<Expr> filters) {
        addToFromStatement(g);
        for (Triple t : triples) {
            addToWhereStatement(t);
        }
        if (optionalTriples != null) {
            for (Triple t : optionalTriples) {
                addOptionalToWhereStatement(t);
            }
        }
        for (String rv : resultVars) {
            addResult(rv);
        }
        for (Expr ef : filters) {
            addFilter(ef);
        }
    }
}
