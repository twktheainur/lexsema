package org.getalp.lexsema.ontolex;


import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.Graph;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.uri.URIParser;

import java.util.List;

/**
 * Generic Interface for LexicalResources
 */
public interface LexicalResource {
    /**
     * @return Returns the base URI of the lexical resource
     */
    String getResourceGraphURI();

    /**
     * Returns the <code>Graph</code> where the lexical resource is stored
     *
     * @return the graph where the lexical resource is stored
     */
    Graph getGraph();

    /**
     * Returns the ontology <code>Model</code> where the lexical resource graph is represented
     *
     * @return the ontology model where the lexical resource is represented
     */
    OntologyModel getModel();

    /**
     * Retrieve the lexical entries matching the entry provided
     *
     * @param entry The entry to search for
     * @return The list of matching lexical entries
     */
    public List<LexicalEntry> getLexicalEntries(String entry);

    /**
     * Retrieve the lexical entries matching the entry and part of speech tag provided
     *
     * @param entry The entry to search for
     * @param pos   A string containing the part of speech tag
     * @return The list of matching lexical entries
     */
    public List<LexicalEntry> getLexicalEntries(String entry, String pos);

    /**
     * Retrieves all the <code>LexicalSense</code>s associated with the <code>LexicalEntry</code> lexicalEntry.
     * @param lexicalEntry The lexical entry for which the lexical senses should be returned
     * @return A list of lexical senses that correspond to the lexical entry
     */
    public List<LexicalSense> getLexicalSenses(LexicalEntry lexicalEntry);


    /**
     * Get a registered URIParser for a <code>LexicalResourceEntity</code> of class <code>entityClass</code>
     *
     * @param entityClass The class of the <code>LexicalResourceEntity</code> for which to retrieve the registered <code>URIParser</code>
     * @return The registered <code>URIParser</code>, null if none are registered for <code>productType</code>
     */
    public URIParser getURIParser(Class<? extends LexicalResourceEntity> entityClass);

    /**
     * Returns the <code>LexicalResourceEntityFactory</code> defined for the present <code>LexicalResource</code>
     *
     * @return The <code>LexicalResourceEntityFactory</code>
     */
    public LexicalResourceEntityFactory getLexicalResourceEntityFactory();

    /**
     * Sets the base URI for the resource graph
     *
     * @param uri The base uri of the resource graph
     */
    public void setURI(String uri);

}
