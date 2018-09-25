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
import org.getalp.lexsema.ontolex.Graph;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for creating and running Select queries through Jena ARQ
 */
public class ARQSelectQueryImpl implements ARQSelectQuery {

    private final Query query;
    private final ElementTriplesBlock whereBlock;
    private final List<ElementTriplesBlock> optionalBlocks;
    private final List<Expr> filters;
    private final Store store;

    /*
     * Internal Initializations
     */ {
        query = QueryFactory.create();
        whereBlock = new ElementTriplesBlock();

        optionalBlocks = new ArrayList<>();
        store = StoreHandler.getStore();
        filters = new ArrayList<>();
    }

    @Override
    public ResultSet runQuery() {
        return runQuery(0);
    }

    @Override
    public ResultSet runQuery(final int limit) {
        final ElementGroup eg = new ElementGroup();
        eg.addElement(whereBlock);
        for (final ElementTriplesBlock ob : optionalBlocks) {
            eg.addElement(new ElementOptional(ob));
        }
        for (final Expr filter : filters) {
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
    public void addToWhereStatement(final Triple triple) {
        whereBlock.addTriple(triple);
    }

    @Override
    public void addOptionalToWhereStatement(final Triple t) {
        final ElementTriplesBlock optionalBlock = new ElementTriplesBlock();
        optionalBlock.addTriple(t);
        optionalBlocks.add(optionalBlock);
    }

    @Override
    public void addResult(final String resultVariable) {
        query.addResultVar(resultVariable);
    }

    @Override
    public void addFilter(final Expr filter) {
        filters.add(filter);
    }

    @Override
    public void addToFromStatement(final Graph graph) {
        final String uri = graph.getJenaNode().toString();
        //query.addGraphURI(uri.substring(0,uri.length()-1));
        //query.addGraphURI(uri);
    }

    /**
     * Set whether the annotresult n-uple should be distinct
     *
     * @param isDistinct when b is true, the DISTINCT keyword is added to the query.
     */
    @SuppressWarnings("all") /*Boolean parameter not recommended unless used in a setter*/
    @Override
    public void setDistinct(boolean isDistinct) {
        query.setDistinct(isDistinct);
    }


    @Override
    public ResultSet runQuery(final Graph graph, final Iterable<Triple> triples, final Iterable<Triple> optionalTriples, final Iterable<String> resultVars) {
        initialize(graph, triples, optionalTriples, resultVars);
        return runQuery();
    }

    @Override
    public void initialize(final Graph graph, final Iterable<Triple> triples, final Iterable<Triple> optionalTriples, final Iterable<String> resultVars) {
        addToFromStatement(graph);
        for (final Triple t : triples) {
            addToWhereStatement(t);
        }
        if (optionalTriples != null && optionalTriples.iterator().hasNext()) {
            for (Triple t : optionalTriples) {
                addOptionalToWhereStatement(t);
            }
        }
        for (String rv : resultVars) {
            addResult(rv);
        }
    }


    @Override
    public ResultSet runQuery(Graph graph, Iterable<Triple> triples, Iterable<Triple> optionalTriples, Iterable<String> resultVars, Iterable<Expr> filters) {
        initialize(graph, triples, optionalTriples, resultVars, filters);
        return runQuery();
    }

    @Override
    public void initialize(Graph graph, Iterable<Triple> triples, Iterable<Triple> optionalTriples, Iterable<String> resultVars, Iterable<Expr> filters) {
        addToFromStatement(graph);
        for (Triple t : triples) {
            addToWhereStatement(t);
        }
        if (optionalTriples != null && optionalTriples.iterator().hasNext()) {
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
