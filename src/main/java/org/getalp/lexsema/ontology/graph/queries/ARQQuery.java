package org.getalp.lexsema.ontology.graph.queries;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.ResultSet;
import org.getalp.lexsema.ontology.graph.Graph;

/**
 * Wrapper for creating and running queries through Jena ARQ
 */
public interface ARQQuery {
    ResultSet runQuery(int limit, String resultVar);

    void addToWhereStatement(Triple t);

    void addResult(String var);

    void addToFromStatement(Graph g);
}
