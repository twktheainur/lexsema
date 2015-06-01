package org.getalp.lexsema.io.word2vec;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.getalp.lexsema.util.Language;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface MultilingualWord2VecLoader {
    public Word2Vec getWord2Vec(Language language);
    public Map<Language,Word2Vec> getWord2Vec();
    public VocabCache getCache(Language language);

    public Set<Language> getLanguages();

    public void load(File directory);
}
