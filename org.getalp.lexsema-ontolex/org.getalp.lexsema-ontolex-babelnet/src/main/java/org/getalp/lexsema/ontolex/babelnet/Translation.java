package org.getalp.lexsema.ontolex.babelnet;


import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.util.Language;

/**
 * An interface for a DBNary translation instance
 */
@SuppressWarnings("unused")
public interface Translation extends LexicalResourceEntity {
    /**
     * Returns the gloss associated to the translation
     *
     * @return The gloss.
     */
    String getGloss();

    /**
     * Set the gloss of the translation
     *
     * @param gloss The gloss of the translation
     */
    void setGloss(String gloss);

    /**
     * Get the translation number as present in Wiktionary
     *
     * @return the translation number
     */
    Integer getTranslationNumber();

    /**
     * Sets the translation number.
     *
     * @param translationNumber The translation number
     */
    void setTranslationNumber(Integer translationNumber);

    /**
     * Get the written form of the translated word
     *
     * @return The written form of the translation
     */
    String getWrittenForm();

    /**
     * Sets the written form pf the translation.
     *
     * @param writtenForm The written form of the translation
     */
    void setWrittenForm(String writtenForm);

    /**
     * Get the target language of the definition
     *
     * @return The target language of the definition
     */
    Language getLanguage();

    void setLanguage(Language language);
}
