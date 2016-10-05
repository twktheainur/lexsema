package org.getalp.lexsema.similarity;

import java.util.Collection;

/**
 * The interface represents a text loaded from a corpus
 */
public interface Text extends Document {

    /**
     * Gives the number of sentences composing the text.
     *
     * @return The number of sentences.
     */
    @Override
    int size();

    /**
     * Returns true of there are zero sentences contained in the text and false otherwise.
     *
     * @return Returns true of there are zero sentences contained in the text and false otherwise.
     */
    @SuppressWarnings("unused")
    boolean isEmpty();


    /**
     * Add a sentence to the text.
     *
     * @param sentence The sentence to add.
     */
    void addSentence(Sentence sentence);

    /**
     * Returns the number of sentences in the text.
     *
     * @return The number of sentences in the text.
     */
    int numberOfSentences();

    Collection<Sentence> sentences();
}
