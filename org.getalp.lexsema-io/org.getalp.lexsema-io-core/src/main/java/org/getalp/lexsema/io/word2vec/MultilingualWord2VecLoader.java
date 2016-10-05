package org.getalp.lexsema.io.word2vec;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.getalp.lexsema.util.Language;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface MultilingualWord2VecLoader {
    WordVectors getWordVectors(Language language);
    Map<Language,WordVectors> getWordVectors();
    VocabCache getCache(Language language);

    Set<Language> getLanguages();

    void load(File directory);
    void loadGoogle(File directory, boolean binary) throws IOException;
}
