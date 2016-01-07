package org.getalp.lexsema.io.word2vec;


import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.util.SerializationUtils;

import java.io.File;
import java.io.IOException;

public class SerializedModelWord2VecLoader implements Word2VecLoader {

    private WordVectors word2Vec;
    private VocabCache vocabCache;

    @Override
    public WordVectors getWordVectors() {
        return word2Vec;
    }

    @Override
    public VocabCache getCache() {
        return vocabCache;
    }

    @Override
    public void load(File directory) {
        File vecPath = new File(directory, "model.ser");
        File cachePath = new File(directory, "cache.ser");
        word2Vec = SerializationUtils.readObject(vecPath);
        //vocabCache = SerializationUtils.readObject(cachePath);
        //word2Vec.
    }

    @Override
    public void loadGoogle(File directory, boolean binary) throws IOException {

        word2Vec = WordVectorSerializer.loadGoogleModel(new File(directory, "model.bin"), binary,true);
        vocabCache = word2Vec.vocab();
    }
}
