package org.getalp.lexsema.io.word2vec;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;

import java.io.File;
import java.io.IOException;

public interface Word2VecLoader {
    WordVectors getWordVectors();

    VocabCache getCache();

    void load(File directory);
    void loadGoogle(File directory, boolean binary, boolean newLines) throws IOException;
}
