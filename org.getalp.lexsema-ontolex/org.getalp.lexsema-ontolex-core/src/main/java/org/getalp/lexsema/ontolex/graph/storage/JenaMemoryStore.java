/**
 *
 */
package org.getalp.lexsema.ontolex.graph.storage;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.getalp.lexsema.ontolex.graph.store.Store;
import virtuoso.jena.driver.VirtGraph;

import java.io.IOException;

public class JenaMemoryStore implements Store {

    private Model model;
    private VirtGraph virtuosoGraph;

    // url = "jdbc:virtuoso://kopi.imag.fr:1982";"dba", "dba"
    public JenaMemoryStore(String aBoxFile) throws IOException {
        model = ModelFactory.createOntologyModel();
        model.read(aBoxFile);
    }

    @Override
    public ResultSet runQuery(Query q) {
        ResultSet rs = null;
        QueryExecution queryExecution = QueryExecutionFactory.create(q, model);
        //System.out.println(queryExecution.getQuery().toString(Syntax.defaultSyntax)); //TODO: Remove
        try {
            rs = queryExecution.execSelect();
        } catch (RuntimeException e) {
            e.printStackTrace(); //TODO: Logging
        }
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
