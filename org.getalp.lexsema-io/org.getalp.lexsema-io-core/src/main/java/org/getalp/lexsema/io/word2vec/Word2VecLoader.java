package org.getalp.lexsema.io.word2vec;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;

import java.io.File;

public interface Word2VecLoader {
    public Word2Vec getWord2Vec();

    public VocabCache getCache();

    public void load(File directory);
}
