package org.getalp.lexsema.similarity;

import org.getalp.lexsema.util.Language;

import java.util.Collection;
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


    Word getWord(int offset, int index);
    Word getWord(int index);

    void addWord(Word word);

    void addWordSenses(Iterable<Sense> senses);

    void addWords(Iterable<Word> words);

    void addWordsSenses(Iterable<Iterable<Sense>> senses);

    List<Sense> getSenses(int offset, int index);

    List<Sense> getSenses(int index);

    int size();

    int indexOfWord(Word word);

    Language getLanguage();

    void setLanguage(Language language);

    Collection<Word> words();
}
