package org.getalp.lexsema.ontology.storage;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import org.getalp.lexsema.ontology.OntologyModel;

/**
 * Interface for a triple store
 */
public interface Store {
    public ResultSet runQuery(Query q);

    public OntologyModel getModel();

    public void close();

}
