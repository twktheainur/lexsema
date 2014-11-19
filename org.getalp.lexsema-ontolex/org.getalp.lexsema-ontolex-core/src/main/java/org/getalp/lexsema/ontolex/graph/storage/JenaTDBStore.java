/**
 *
 */
package org.getalp.lexsema.ontolex.graph.storage;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.getalp.lexsema.ontolex.graph.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JenaTDBStore implements Store {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Dataset dataset;
    private Model model;

    // url = "jdbc:virtuoso://kopi.imag.fr:1982";"dba", "dba"
    public JenaTDBStore(String datasetPath) throws IOException {
        dataset = TDBFactory.createDataset(datasetPath);
        dataset.begin(ReadWrite.READ);
        model = dataset.getDefaultModel();
        dataset.end();

    }

    @Override
    public ResultSet runQuery(Query q) {
        if (q.getQueryType() == Query.QueryTypeAsk ||
                q.getQueryType() == Query.QueryTypeDescribe ||
                q.getQueryType() == Query.QueryTypeSelect) {
            dataset.begin(ReadWrite.READ);
        } else if (q.getQueryType() == Query.QueryTypeConstruct) {
            dataset.begin(ReadWrite.WRITE);
        }
        ResultSet rs = null;
        QueryExecution queryExecution = QueryExecutionFactory.create(q, model);
        logger.info(queryExecution.getQuery().toString(Syntax.defaultSyntax));
        try {
            rs = queryExecution.execSelect();
        } catch (RuntimeException e) {
            logger.error(e.getLocalizedMessage());
        }
        dataset.end();
        return rs;
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
