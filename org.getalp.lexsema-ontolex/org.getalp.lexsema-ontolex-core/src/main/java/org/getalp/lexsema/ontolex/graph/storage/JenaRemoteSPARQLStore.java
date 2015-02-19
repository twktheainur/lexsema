/**
 *
 */
package org.getalp.lexsema.ontolex.graph.storage;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JenaRemoteSPARQLStore implements Store {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Model model;
    private String endpoint;

    public JenaRemoteSPARQLStore(String endpointURI) throws IOException {
        model = ModelFactory.createOntologyModel();
        endpoint = endpointURI;
    }

    @Override
    public ResultSet runQuery(Query q) {
        ResultSet rs = null;
        QueryExecution queryExecution = new QueryEngineHTTP(endpoint, q);
        if (StoreHandler.DEBUG_ON) {
            logger.info(queryExecution.getQuery().toString(Syntax.defaultSyntax));
        }
        try {
            rs = queryExecution.execSelect();
        } catch (RuntimeException e) {
            logger.error(e.getLocalizedMessage());
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

    @SuppressWarnings("BooleanParameter")
    @Override
    public void setCachingEnabled(boolean cachingEnabled) {

    }
}
