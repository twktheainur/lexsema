package org.getalp.lexsema.io.text;

import org.getalp.lexsema.similarity.Sentence;

public interface SentenceProcessor {
    /**
     * Reads a sentence and applies a processing pipeline to it.
     */
    public Sentence process(String sentenceText, String documentId, String language);

}
