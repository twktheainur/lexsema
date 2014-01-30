package org.getalp.lexsema.ontology.storage;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * Interface for a triple store
 */
public interface Store {
    public ResultSet runQuery(Query q);

    public Model getABox();

    public void close();

}
