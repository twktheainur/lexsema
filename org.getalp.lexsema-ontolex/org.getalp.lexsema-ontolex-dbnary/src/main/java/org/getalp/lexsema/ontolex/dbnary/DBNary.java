package org.getalp.lexsema.ontolex.dbnary;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;

import java.util.List;
import java.util.Locale;

/**
 * Access interface for the DBNary API
 */
public interface DBNary extends LexicalResource {
    /**
     * Retrieve the vocable instance that corresponds to the <code>vocable</code> string
     *
     * @param vocable the vocable string for which the instance needs to be retrieved
     * @return The <code>Vocable</code> instance
     * @throws NoSuchVocableException Thrown when the vocable does not exist in the resource
     */
    @SuppressWarnings("unused")
    Vocable getVocable(String vocable) throws NoSuchVocableException;

    /**
     * Returns a list of all vocables present in the resource
     *
     * @return a list of all the vocable present in the resource
     */
    @SuppressWarnings("unused")
    List<Vocable> getVocables();

    /**
     * Retrieve the lexical entries matching the entry, the part of speech tag as well as the entry number
     * In some resources (e.g. Wiktionary derivatives), there can be two entries with the same name and part
     * of speech that are distinguished by a number. This is the case for example for two homonyms or words with
     * different etymologies.
     *
     * @param entry The entry to search for
     * @param pos   A string containing the part of speech tag
     * @return The list of matching lexical entries
     */
    @SuppressWarnings("unused")
    List<LexicalEntry> getLexicalEntries(String entry, String pos, int entryNumber);

    /**
     * Returns the list of <code>LexicalEntry</code>(ies) associated (dbnary:relatedTo) with the <code>Vocable</code> <code>vocable</code>
     *
     * @return the list of LexicalEntries associated (dbnary:relatedTo) with the Vocable
     */
    @SuppressWarnings("unused")
    public List<LexicalEntry> getLexicalEntries(Vocable vocable);

    /**
     * Retrieves translations for the current <code>LexicalResourceEntity</code> in a given <code>language</code>
     *
     * @param sourceEntity The source lexical resource entity
     * @param language     The desired language of the translation, if the language is null, all entities are retrieved
     * @return A <code>List</code> of <code>Translation</code>s for the given entity and language, returns null when there
     * are no translation associated to the entity for the particular language.
     */
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Locale language);
}
