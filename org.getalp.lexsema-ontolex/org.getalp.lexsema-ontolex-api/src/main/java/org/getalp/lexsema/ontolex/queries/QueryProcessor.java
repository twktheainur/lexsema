package org.getalp.lexsema.ontolex.queries;


import java.util.List;

/**
 * Defines a generic query processor that defines a query, allows to run it and process the results
 * in such a way so as to return a list of T objects that represent the data retrieved from the query
 *
 * @param <T> The type of the objects to return. They correspond to a type that represents the semantic of the
 *            information retrieved from the query.
 */
public interface QueryProcessor<T> {
    /**
     * Runs the query
     */
    public void runQuery();

    /**
     * Processes the results and encapsulates them in <code>T</code> instances.
     *
     * @return The list of resulting <code>T</code> instances
     */
    public List<T> processResults();

    /**
     * Retrieves the query serviced by the <code>QueryProcessor</code>
     *
     * @return The query serviced by the <code>QueryProcessor</code>
     */
    public ARQQuery getQuery();
}
