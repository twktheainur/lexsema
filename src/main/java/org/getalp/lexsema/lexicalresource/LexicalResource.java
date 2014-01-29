package org.getalp.lexsema.lexicalresource;

import org.getalp.lexsema.lexicalresource.lemon.LemonEntityFactory;
import org.getalp.lexsema.lexicalresource.lemon.LexicalEntry;
import org.getalp.lexsema.ontology.graph.Graph;

import java.util.List;

/**
 * Generic Interface for LexicalResources
 */
public interface LexicalResource extends LemonEntityFactory {
    /**
     * @return Returns the base URI of the lexical resource
     */
    String getURI();

    /**
     * Returns the <code>Graph</code> where the lexical resource is stored
     *
     * @return the graph where the lexical resource is stored
     */
    Graph getGraph();

    /**
     * Retrieve the lexical entries matching the entry provided
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
     * Retrieve the lexical entries matching the entry, the part of speech tag as well as the entry number
     * In some resources (e.g. wikitionary derivatives), there can be two entries with the same name and part
     * of speech that are distinguished by a number. This is the case for example for two homonyms or words with
     * different etymologies.
     *
     * @param entry The entry to search for
     * @param pos   A string containing the part of speech tag
     * @return The list of matching lexical entries
     */
    public List<LexicalEntry> getLexicalEntries(String entry, String pos, int entryNumber);
}
