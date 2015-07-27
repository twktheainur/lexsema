package org.getalp.lexsema.ontolex;

import org.getalp.lexsema.util.Language;

/**
 * A Lemon LexicalEntry Java Wrapper Class
 */
public class LexicalEntryImpl extends AbstractLexicalResourceEntity implements LexicalEntry {

    private String lemma;
    private String partOfSpeech;
    private int number = 0;

    private Language language;

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
        language = r.getLanguage();
    }

    @Override
    public String toString() {
        String localPOS = partOfSpeech.split("#")[1];
        return String.format("%s LexicalEntry|%s#%s|", language, lemma, localPOS);
    }

    @Override
    public String getLemma() {
        return lemma;
    }

    @Override
    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    @Override
    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    @Override
    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }
}
