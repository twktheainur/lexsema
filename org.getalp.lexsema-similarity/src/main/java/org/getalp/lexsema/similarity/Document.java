package org.getalp.lexsema.similarity;

import org.getalp.lexsema.util.Language;

import java.util.List;

/**
 * Interface that represents a document (Sentence or Text).
 */
public interface Document extends Iterable<Word> {
    /**
     * Returns the ID of the document.
     *
     * @return The id of the document.
     */
    String getId();

    /**
     * Sets the id of the document.
     *
     * @param id the id of the document.
     */
    void setId(String id);


    public Word getWord(int offset, int index);
    public Word getWord(int index);

    public void addWord(Word w);

    public void addWordSenses(Iterable<Sense> s);

    public void addWords(Iterable<Word> w);

    public void addWordsSenses(Iterable<Iterable<Sense>> s);

    List<Sense> getSenses(int offset, int index);

    List<Sense> getSenses(int index);

    public int size();

    public int indexOfWord(Word w);

    public Language getLanguage();

    public void setLanguage(Language language);
}
