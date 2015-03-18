package org.getalp.lexsema.acceptali.word2vec;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.getalp.lexsema.language.Language;

import java.io.File;
import java.util.Set;

public interface MultilingualWord2VecLoader {
    public Word2Vec getWord2Vec(Language language);

    public VocabCache getCache(Language language);

    public Set<Language> getLanguages();

    public void load(File directory);
}
