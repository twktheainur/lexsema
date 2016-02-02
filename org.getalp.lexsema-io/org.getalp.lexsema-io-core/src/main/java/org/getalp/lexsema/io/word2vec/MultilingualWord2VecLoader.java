package org.getalp.lexsema.io.word2vec;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.getalp.lexsema.util.Language;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface MultilingualWord2VecLoader {
    public WordVectors getWordVectors(Language language);
    public Map<Language,WordVectors> getWordVectors();
    public VocabCache getCache(Language language);

    public Set<Language> getLanguages();

    public void load(File directory);
    public void loadGoogle(File directory, boolean binary) throws IOException;
}
