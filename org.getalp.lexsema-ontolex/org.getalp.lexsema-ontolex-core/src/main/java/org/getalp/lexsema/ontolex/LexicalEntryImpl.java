package org.getalp.lexsema.ontolex;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A Lemon LexicalEntry Java Wrapper Class
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LexicalEntryImpl extends AbstractLexicalResourceEntity implements LexicalEntry {
    /**
     * --GETTER
     *
     * @return Returns the lemma of the <code>LexicalEntry</code>
     * --SETTER
     * @param lemma Sets the lemma of the <code>LexicalEntry</code>
     */
    private String lemma;
    /**
     * --GETTER
     *
     * @return Returns the part of speech tag of the <code>LexicalEntry</code>
     * --SETTER
     */
    private String partOfSpeech;
    private int number;

    /**
     * Constructor
     *
     * @param r   The lexical resource in which the LexicalEntry is situated
     * @param uri The uri of the LexicalEntry
     */
    public LexicalEntryImpl(LexicalResource r, String uri, LexicalResourceEntity parent, String lemma, String partOfSpeech) {
        super(r, uri, parent);
        this.lemma = lemma;
        this.partOfSpeech = partOfSpeech;
    }
}
