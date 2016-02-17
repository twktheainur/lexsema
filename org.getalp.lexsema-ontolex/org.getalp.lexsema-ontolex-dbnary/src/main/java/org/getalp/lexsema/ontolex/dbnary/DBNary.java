package org.getalp.lexsema.ontolex.dbnary;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.ontolex.dbnary.relations.DBNaryRelationType;
import org.getalp.lexsema.util.Language;

import java.util.List;

/**
 * Access interface for the DBNary API
 */
public interface DBNary extends LexicalResource {
    /**
     * Retrieve the vocable instance that corresponds to the {@code vocable} string
     *
     * @param vocable the vocable string for which the instance needs to be retrieved
     * @return The {@code Vocable} instance
     * @throws NoSuchVocableException Thrown when the vocable does not exist in the resource
     */
    @SuppressWarnings("unused")
    Vocable getVocable(String vocable) throws NoSuchVocableException;

    /**
     * Retrieve the vocable instance that corresponds to the {@code vocable} string
     *
     * @param vocable  the vocable string for which the instance needs to be retrieved
     * @param language the language of the vocable
     * @return The {@code Vocable} instance
     * @throws NoSuchVocableException Thrown when the vocable does not exist in the resource
     */
    @SuppressWarnings("unused")
    Vocable getVocable(String vocable, Language language) throws NoSuchVocableException;

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
     * @param entry       The entry to search for
     * @param pos         A string containing the part of speech tag
     * @param entryNumber the entry number (see method description)
     * @return The list of matching lexical entries
     */
    @SuppressWarnings("unused")
    List<LexicalEntry> getLexicalEntries(String entry, String pos, int entryNumber);

    /**
     * Retrieve the lexical entries matching the entry, the part of speech tag as well as the entry number
     * In some resources (e.g. Wiktionary derivatives), there can be two entries with the same name and part
     * of speech that are distinguished by a number. This is the case for example for two homonyms or words with
     * different etymologies.
     *
     * @param entry       The entry to search for
     * @param pos         A string containing the part of speech tag
     * @param entryNumber the entry number (see method description)
     * @param language    the language of the lexical entry
     * @return The list of matching lexical entries
     */
    @SuppressWarnings("unused")
    List<LexicalEntry> getLexicalEntries(String entry, String pos, int entryNumber, Language language);


    /**
     * Returns the list of {@code LexicalEntry}(ies) associated (dbnary:relatedTo) with the {@code Vocable} {@code vocable}
     *
     * @return the list of LexicalEntries associated (dbnary:relatedTo) with the Vocable
     */
    @SuppressWarnings("unused")
    public List<LexicalEntry> getLexicalEntries(Vocable vocable);

    /**
     * Retrieves translations for the current {@code LexicalResourceEntity} in a given {@code language}
     *
     * @param sourceEntity The source lexical resource entity
     * @param language     The desired language of the translation, if the language is null, all entities are retrieved
     * @return A {@code List} of {@code Translation}s for the given entity and language, returns null when there
     * are no translation associated to the entity for the particular language.
     */
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Language language);

    /**
     * Retrieves translations for the current {@code LexicalResourceEntity} in a given {@code language}
     *
     * @param sourceEntity The source lexical resource entity
     * @param languages    The desired languages of the translations
     * @return A {@code List} of {@code Translation}s for the given entity and language, returns null when there
     * are no translation associated to the entity for the particular languages.
     */
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Language... languages);

    /**
     * Retrieves translations for the current {@code LexicalResourceEntity} in a given {@code language}
     *
     * @param sourceEntity The source lexical resource entity
     * @return A {@code List} of {@code Translation}s for the given entity and language, returns null when there
     * are no translation associated to the entity for the particular language.
     */
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity);

    /**
     * Retrieve the related {@code LexicalResourceEntity} (ies) associated
     * to {@code sourceEntity} through {@code relationType}
     *
     * @param sourceEntity The source {@code LexicalResourceEntity}
     * @param relationType The relation type
     * @return The list of  entities related to the source entity through the provided relation type.
     */
    public List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, DBNaryRelationType relationType);

    /**
     * Retrieve the related {@code LexicalResourceEntity} (ies) associated
     * to {@code sourceEntity} through {@code relationType}
     *
     * @param sourceEntity     The source {@code LexicalResourceEntity}
     * @param relationTypeList The list relation types
     * @return The list of  entities related to the source entity through the provided relation types.
     */
    List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, List<DBNaryRelationType> relationTypeList);
}
