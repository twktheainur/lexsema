package org.getalp.lexsema.ontolex.graph.queries;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.ResultSet;
import org.getalp.lexsema.ontolex.graph.Graph;

/**
 * Wrapper for creating and running queries through Jena ARQ
 */
public interface ARQQuery {

    /**
     * Run a SPARQL Query
     *
     * @param limit Maximum number of results retrieved
     * @return Returns a set of a number <code>limit</code> of the results
     */
    ResultSet runQuery(int limit);

    /**
     * Run a SPARQL Query
     *
     * @return Returns a set of all the results
     */
    ResultSet runQuery();

    /**
     * Add a condition triple to the where statement the query
     *
     * @param t The triple to add to the where statement
     */
    void addToWhereStatement(Triple t);

    /**
     * Adds a variable as a result variable in the ResultSet
     *
     * @param var The variable name to add
     */
    void addResult(String var);

    /**
     * Adds a from statement having for target the URI in <code>Graph</code> <code>g</code>
     *
     * @param g the graph the uri of which is to be added to the query
     */
    void addToFromStatement(Graph g);
}
