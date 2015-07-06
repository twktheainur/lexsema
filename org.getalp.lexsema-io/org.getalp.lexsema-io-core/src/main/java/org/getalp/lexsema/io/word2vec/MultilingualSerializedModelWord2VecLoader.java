package org.getalp.lexsema.io.word2vec;


import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultilingualSerializedModelWord2VecLoader implements MultilingualWord2VecLoader {

    private static Logger logger = LoggerFactory.getLogger(MultilingualSerializedModelWord2VecLoader.class);
    private Map<Language, Word2Vec> word2VecMap = new HashMap<>();
    private Map<Language, VocabCache> vocabCacheMap = new HashMap<>();

    @Override
    public Word2Vec getWord2Vec(Language language) {
        return word2VecMap.get(language);
    }

    @Override
    public Map<Language, Word2Vec> getWord2Vec() {
        return Collections.unmodifiableMap(word2VecMap);
    }

    @Override
    public VocabCache getCache(Language language) {
        return vocabCacheMap.get(language);
    }

    @Override
    public Set<Language> getLanguages() {
        return word2VecMap.keySet();
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void load(File directory) {
        if (directory != null) {
            //noinspection ConstantConditions
            for (File file : directory.listFiles()) {

                String langCode = file.getName();
                Language language = Language.fromCode(langCode);
                Word2VecLoader word2VecLoader = new SerializedModelWord2VecLoader();
                logger.info(String.format("\tLoading %s model...", language.toString()));
                word2VecLoader.load(file);
                word2VecMap.put(language, word2VecLoader.getWord2Vec());
                vocabCacheMap.put(language, word2VecLoader.getCache());
            }
        }
    }

    @Override
    public void loadGoogle(File directory, boolean binary) throws IOException {
        if (directory != null) {
            //noinspection ConstantConditions
            for (File file : directory.listFiles()) {

                String langCode = file.getName();
                Language language = Language.fromCode(langCode);
                Word2VecLoader word2VecLoader = new SerializedModelWord2VecLoader();
                logger.info(String.format("\tLoading %s model...", language.toString()));
                word2VecLoader.loadGoogle(file, binary);
                word2VecMap.put(language, word2VecLoader.getWord2Vec());
                vocabCacheMap.put(language, word2VecLoader.getCache());
            }
        }
    }
}
