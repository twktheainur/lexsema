/**
 *
 */
package org.getalp.lexsema.ontolex.graph.storage;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.getalp.lexsema.ontolex.graph.store.QueryCache;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JenaTDBStore implements Store {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    QueryCache cache;
    private Dataset dataset;
    private Model model;
    private boolean cachingEnabled;

    // url = "jdbc:virtuoso://kopi.imag.fr:1982";"dba", "dba"
    public JenaTDBStore(String datasetPath) throws IOException {
        dataset = TDBFactory.createDataset(datasetPath);
        dataset.begin(ReadWrite.READ);
        model = dataset.getDefaultModel();
        dataset.end();
        if (cachingEnabled) {
            cache = new DefaultQueryCache();
        }

    }

    @Override
    public ResultSet runQuery(Query q) {
        ResultSet rs = null;
        if (!cachingEnabled || !cache.hasResult(q.toString())) {
            if (q.getQueryType() == Query.QueryTypeAsk ||
                    q.getQueryType() == Query.QueryTypeDescribe ||
                    q.getQueryType() == Query.QueryTypeSelect) {
                dataset.begin(ReadWrite.READ);
            } else if (q.getQueryType() == Query.QueryTypeConstruct) {
                dataset.begin(ReadWrite.WRITE);
            }

            QueryExecution queryExecution = QueryExecutionFactory.create(q, model);
            if (StoreHandler.DEBUG_ON) {
                logger.info(String.format(System.lineSeparator() +
                        "%s", queryExecution.getQuery().toString(Syntax.defaultSyntax)));
            }
            try {
                rs = queryExecution.execSelect();
            } catch (RuntimeException e) {
                logger.error(e.getLocalizedMessage());
            }
            dataset.end();
            if (cachingEnabled) {
                rs = new CachableResultSet(rs);
                cache.addResult(q.toString(), rs);
            }
        } else {
            rs = cache.retrieveResult(q.toString());
        }

        return rs;
    }

    @Override
    @SuppressWarnings("BooleanParameters")
    public void setCachingEnabled(boolean cachingEnabled) {
        this.cachingEnabled = cachingEnabled;
    }

    @Override
    public Model getABox() {
        return dataset.getDefaultModel();
    }

    @Override
    public synchronized void close() {
        dataset.close();
        model.close();

    }
}
