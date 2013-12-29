/**
 *
 */
package org.getalp.lexsema.ontology.storage;

import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import org.getalp.lexsema.ontology.DefaultOntologyModel;
import org.getalp.lexsema.ontology.OntologyModel;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryEngine;

import java.io.IOException;

public class VirtuosoTripleStore implements Store {

    private OntologyModel model;
    private VirtGraph virtuosoGraph;

    // url = "jdbc:virtuoso://kopi.imag.fr:1982";"dba", "dba"
    public VirtuosoTripleStore(String uri, String username, String password) throws IOException {
        virtuosoGraph = new VirtGraph(uri, username, password);
        Model virtuosoModel = new VirtModel(virtuosoGraph);
        model = new DefaultOntologyModel(virtuosoModel);
    }

    @Override
    public ResultSet runQuery(Query q) {
        ResultSet rs = null;
        TransactionHandler th = virtuosoGraph.getTransactionHandler();
        VirtuosoQueryEngine.register();
        th.begin();
        QueryExecution queryExecution = QueryExecutionFactory.create(q.toString(), model.getJenaModel());
        System.out.println(queryExecution.getQuery().toString(Syntax.defaultSyntax)); //TODO: Remove
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
    public OntologyModel getModel() {
        return model;
    }

    @Override
    public synchronized void close() {
        model.getJenaModel().commit();
        model.getJenaModel().getBaseModel().commit();
        model.getJenaModel().getBaseModel().close();
        model.getJenaModel().close();
    }
}
