package org.getalp.lexsema.ontolex;


import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.ontolex.factories.entities.LexicalResourceEntityFactory;
import org.getalp.lexsema.ontolex.graph.OntologyModel;
import org.getalp.lexsema.ontolex.uri.URIParser;

import java.io.Serializable;
import java.util.List;

/**
 * Generic Interface for LexicalResources
 */
public interface LexicalResource extends Serializable{
    /**
     * @return Returns the base URI of the lexical resource
     */
    String getResourceGraphURI();

    /**
     * @return Returns the base URI of the lexical resource
     */
    String getResourceGraphURI(Language language);

    /**
     * Returns the {@code Graph} where the lexical resource is stored
     *
     * @return the graph where the lexical resource is stored
     */
    Graph getGraph();

    /**
     * Returns the {@code Graph} where the lexical resource is stored
     *
     * @param language the graph corresponding to this language
     * @return the graph where the lexical resource is stored
     */
    Graph getGraph(Language language);

    /**
     * Returns the ontology {@code Model} where the lexical resource graph is represented
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
    List<LexicalEntry> getLexicalEntries(String entry);

    /**
     * Retrieve the lexical entries matching the entry provided
     *
     * @param entry    The entry to search for
     * @param language The language for which to retrieve the entry
     * @return The list of matching lexical entries
     */
    List<LexicalEntry> getLexicalEntries(String entry, Language language);

    /**
     * Retrieve the lexical entries matching the entry and part of speech tag provided
     *
     * @param entry The entry to search for
     * @param pos   A string containing the part of speech tag
     * @return The list of matching lexical entries
     */
    List<LexicalEntry> getLexicalEntries(String entry, String pos);

    /**
     * Retrieve the lexical entries matching the entry and part of speech tag provided
     *
     * @param entry    The entry to search for
     * @param pos      A string containing the part of speech tag
     * @param language The language for which to retrieve the entry
     * @return The list of matching lexical entries
     */
    List<LexicalEntry> getLexicalEntries(String entry, String pos, Language language);

    /**
     * Retrieves all the {@code LexicalSense}s associated with the {@code LexicalEntry} lexicalEntry.
     *
     * @param lexicalEntry The lexical entry for which the lexical senses should be returned
     * @return A list of lexical senses that correspond to the lexical entry
     */
    List<LexicalSense> getLexicalSenses(LexicalEntry lexicalEntry);


    /**
     * Get a registered URIParser for a {@code LexicalResourceEntity} of class {@code entityClass}
     *
     * @param entityClass The class of the {@code LexicalResourceEntity} for which to retrieve the registered {@code URIParser}
     * @return The registered {@code URIParser}, null if none are registered for {@code productType}
     */
    URIParser getURIParser(Class<? extends LexicalResourceEntity> entityClass);

    /**
     * Returns the {@code LexicalResourceEntityFactory} defined for the present {@code LexicalResource}
     *
     * @return The {@code LexicalResourceEntityFactory}
     */
    LexicalResourceEntityFactory getLexicalResourceEntityFactory();

    /**
     * Returns the language of the lexical resource, null if the resource is not bound to a particualr language
     *
     * @return The language of the resource.
     */
    Language getLanguage();

}
