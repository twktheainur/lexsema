package org.getalp.lexsema.ontolex.queries;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.expr.Expr;
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

    /**
     * Adds an expression as a filter to the query
     *
     * @param filter The expression to ass as a filter.
     */
    void addFilter(Expr filter);

    /**
     * Initialize the query on <code>Graph</code> <code>g</code> with where statement <code>Triple</code> defined in <code>triples</code>
     * and result variables defined in <code>resultVars</code>
     *
     * @param g          The <code>Graph</code> from which to query
     * @param triples    The triples to add to the WHERE statement
     * @param resultVars Result variables to consider for the output
     * @return The results of the query
     */
    public ResultSet runQuery(Graph g, Iterable<Triple> triples, Iterable<String> resultVars);

    /**
     * Initialize the query on <code>Graph</code> <code>g</code> with where statement <code>Triple</code> defined in <code>triples</code>
     * and result variables defined in <code>resultVars</code>. <code>filters</code> is a list of FILTER expressions for
     * the query.
     *
     * @param g          The <code>Graph</code> from which to query
     * @param triples    The triples to add to the WHERE statement
     * @param resultVars Result variables to consider for the output
     * @param filters    The filter list.
     * @return The results of the query
     */
    public ResultSet runQuery(Graph g, Iterable<Triple> triples, Iterable<String> resultVars, Iterable<Expr> filters);

    /**
     * Initialize the query on <code>Graph</code> <code>g</code> with where statement <code>Triple</code> defined in <code>triples</code>
     * and result variables defined in <code>resultVars</code>
     *
     * @param g          The <code>Graph</code> from which to query
     * @param triples    The triples to add to the WHERE statement
     * @param resultVars Result variables to consider for the output
     */
    public void initialize(Graph g, Iterable<Triple> triples, Iterable<String> resultVars);

    /**
     * Initialize the query on <code>Graph</code> <code>g</code> with where statement <code>Triple</code> defined in <code>triples</code>
     * and result variables defined in <code>resultVars</code>. <code>filters</code> is a list of FILTER expressions for
     * the query.
     *
     * @param g          The <code>Graph</code> from which to query.
     * @param triples    The triples to add to the WHERE statement.
     * @param resultVars Result variables to consider for the output.
     * @param filters    The filter list.
     */
    public void initialize(Graph g, Iterable<Triple> triples, Iterable<String> resultVars, Iterable<Expr> filters);

}
