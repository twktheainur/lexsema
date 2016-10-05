package org.getalp.lexsema.io.text;

import org.getalp.lexsema.similarity.Text;

public interface TextProcessor {
    /**
     * Reads raw text and applies a processing pipeline to it.
     */
    Text process(String sentenceText, String documentId);

}
