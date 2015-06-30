package org.getalp.lexsema.ontolex.babelnet;


import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.babelnet.relations.LexinfoRelationType;
import org.getalp.lexsema.util.Language;

import java.util.List;

/**
 * Access interface for the DBNary API
 */
public interface BabelNet extends LexicalResource {
    /**
     * Retrieves translations for the current <code>LexicalResourceEntity</code> in a given <code>language</code>
     *
     * @param sourceEntity The source lexical resource entity
     * @param language     The desired language of the translation, if the language is null, all entities are retrieved
     * @return A <code>List</code> of <code>Translation</code>s for the given entity and language, returns null when there
     * are no translation associated to the entity for the particular language.
     */
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity, Language language);

    /**
     * Retrieves translations for the current <code>LexicalResourceEntity</code> in a given <code>language</code>
     *
     * @param sourceEntity The source lexical resource entity
     * @return A <code>List</code> of <code>Translation</code>s for the given entity and language, returns null when there
     * are no translation associated to the entity for the particular language.
     */
    public List<Translation> getTranslations(LexicalResourceEntity sourceEntity);

    /**
     * Retrieve the related <code>LexicalResourceEntity</code> (ies) associated
     * to <code>sourceEntity</code> through <code>relationType</code>
     *
     * @param sourceEntity The source <code>LexicalResourceEntity</code>
     * @param relationType The relation type
     * @return The list of  entities related to the source entity through the provided relation type.
     */
    public List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, LexinfoRelationType relationType);

    /**
     * Retrieve the related <code>LexicalResourceEntity</code> (ies) associated
     * to <code>sourceEntity</code> through <code>relationType</code>
     *
     * @param sourceEntity     The source <code>LexicalResourceEntity</code>
     * @param relationTypeList The list relation types
     * @return The list of  entities related to the source entity through the provided relation types.
     */
    List<LexicalResourceEntity> getRelatedEntities(LexicalResourceEntity sourceEntity, List<LexinfoRelationType> relationTypeList);
}
