/**
 *
 */
package org.getalp.lexsema.ontolex.graph.storage;

import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import org.getalp.lexsema.ontolex.graph.store.Store;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryEngine;

import java.io.IOException;

@SuppressWarnings("unused")
public class VirtuosoStore implements Store {

    private Model model;
    private VirtGraph virtuosoGraph;

    // url = "jdbc:virtuoso://kopi.imag.fr:1982";"dba", "dba"
    public VirtuosoStore(String uri, String username, String password) throws IOException {
        virtuosoGraph = new VirtGraph(uri, username, password);
        model = new VirtModel(virtuosoGraph);

    }

    @Override
    public ResultSet runQuery(Query q) {
        ResultSet rs = null;
        TransactionHandler th = virtuosoGraph.getTransactionHandler();
        VirtuosoQueryEngine.register();
        th.begin();
        QueryExecution queryExecution = QueryExecutionFactory.create(q, model);
        //System.out.println(queryExecution.getQuery().toString(Syntax.defaultSyntax)); //TODO: Remove
        try {
            rs = queryExecution.execSelect();
        } catch (RuntimeException e) {
            e.printStackTrace(); //TODO: Logging
        }
        th.commit();
        VirtuosoQueryEngine.unregister();
        return rs;
    }

    @Override
    public Model getABox() {
        return model;
    }

    @Override
    public synchronized void close() {
        model.commit();
        model.close();
    }
}
