package org.getalp.lexsema.ontolex.queries;

/**
 * Interface for a Select SPARQL Query Wrapper for Jena ARQ
 */
public interface ARQSelectQuery extends ARQQuery {
    /**
     * Sets whether or not the results of the SELECT query should be distinct by adding the DISTINCT keyword
     *
     * @param b Add the distinct keyword to the query?
     */
    @SuppressWarnings("BooleanParameter")
    void setDistinct(boolean b);


}
