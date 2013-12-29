package org.getalp.lexsema.ontology.graph.queries;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import org.getalp.lexsema.ontology.graph.Graph;
import org.getalp.lexsema.ontology.storage.Store;
import org.getalp.lexsema.ontology.storage.StoreHandler;

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
    public ResultSet runQuery(int limit, String resultVar) {
        ElementGroup eg = new ElementGroup();
        eg.addElement(whereBlock);

        query.setQueryPattern(eg);
        query.setQuerySelectType();
        query.setLimit(limit);
        query.addResultVar(resultVar);
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
        query.addGraphURI(g.getUri().toString());
    }
}
