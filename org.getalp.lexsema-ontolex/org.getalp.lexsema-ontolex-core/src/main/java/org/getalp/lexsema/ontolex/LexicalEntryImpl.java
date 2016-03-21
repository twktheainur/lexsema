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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LexicalEntryImpl)) return false;
        if (!super.equals(o)) return false;

        LexicalEntryImpl that = (LexicalEntryImpl) o;

        if (getNumber() != that.getNumber()) return false;
        if (getLemma() != null ? !getLemma().equals(that.getLemma()) : that.getLemma() != null) return false;
        if (getPartOfSpeech() != null ? !getPartOfSpeech().equals(that.getPartOfSpeech()) : that.getPartOfSpeech() != null)
            return false;
        return getLanguage() == that.getLanguage();

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getLemma() != null ? getLemma().hashCode() : 0);
        result = 31 * result + (getPartOfSpeech() != null ? getPartOfSpeech().hashCode() : 0);
        result = 31 * result + getNumber();
        result = 31 * result + (getLanguage() != null ? getLanguage().hashCode() : 0);
        return result;
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
