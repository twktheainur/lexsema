package org.getalp.lexsema.ontolex.graph.queries;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.graph.storage.StoreHandler;
import org.getalp.lexsema.ontolex.graph.store.Store;

/**
 * Wrapper for creating and running Select queries through Jena ARQ
 */
public class ARQSelectQuery implements ARQQuery {

    private Query query;
    ElementTriplesBlock whereBlock;


    /*
     * Internal Initializations
     */ {
        query = QueryFactory.create();
        whereBlock = new ElementTriplesBlock();
    }

    public ARQSelectQuery() {
    }

    @Override
    public ResultSet runQuery() {
        return runQuery(0);
    }

    @Override
    public ResultSet runQuery(int limit) {
        ElementGroup eg = new ElementGroup();
        eg.addElement(whereBlock);

        query.setQueryPattern(eg);
        query.setQuerySelectType();
        if (limit > 0) {
            query.setLimit(limit);
        }
        Store vts = StoreHandler.getStore();
        return vts.runQuery(query);
    }

    @Override
    public void addToWhereStatement(Triple t) {
        whereBlock.addTriple(t);
    }

    @Override
    public void addResult(String var) {
        query.addResultVar(var);
    }

    @Override
    public void addToFromStatement(Graph g) {
        String uri = g.getUri().toString();
        //query.addGraphURI(uri.substring(0,uri.length()-1));
        query.addGraphURI(uri);
    }

    /**
     * Set whether the result n-uple should be distinct
     *
     * @param b when b is true, the DISTINCT keyword is added to the query.
     */
    public void setDistinct(boolean b) {
        query.setDistinct(b);
    }
}
