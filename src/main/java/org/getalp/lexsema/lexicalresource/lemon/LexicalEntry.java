/**
 *
 */
package org.getalp.lexsema.lexicalresource.lemon;

import org.getalp.lexsema.lexicalresource.AbstractLexicalResourceEntity;
import org.getalp.lexsema.lexicalresource.LexicalResource;

/**
 * A Lemon LexicalEntry Java Wrapper Class
 */
public class LexicalEntry extends AbstractLexicalResourceEntity {

    private String lemma;
    private String partOfSpeech;
    private int number;


    /**
     * Constructor
     *
     * @param r   The lexical resource in which the LexicalEntry is situated
     * @param uri The uri of the LexicalEntry
     */
    public LexicalEntry(LexicalResource r, String uri) {
        super(r, uri);
    }

    /**
     * @return The lemma of the LexicalEntry
     */
    public String getLemma() {
        return lemma;
    }

    /**
     * Sets the lemma of the LexicalEntry
     *
     * @param lemma The lemma to set
     */
    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    /**
     * @return Returns the part of speech tag of the LexicalEntry
     */
    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    /**
     * Sets the part of speech tag
     *
     * @param partOfSpeech The part of speech tag to set
     */
    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    /**
     * @return Returns the LexicalEntry number, 0 if not present
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Sets the number of the LExicalEntry for the same POS and Lemma
     *
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String
    toString() {
        return "LexicalEntry{" +
                "'" + lemma + '\'' +
                ", partOfSpeech='" + partOfSpeech + '\'' +
                ", number=" + number +
                '}';
    }
}
