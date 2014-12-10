package org.getalp.lexsema.similarity.cache;


import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SenseCache {

    private static SenseCache instance;
    private Map<Word, List<Sense>> cache;

    private SenseCache() {
        cache = new HashMap<>();
    }

    public static SenseCache getInstance() {
        if (instance == null) {
            instance = new SenseCache();
        }
        return instance;
    }

    public List<Sense> getSenses(Word w) {
        return cache.get(w);
    }

    public void addToCache(Word w, List<Sense> ls) {
        cache.put(w, ls);
    }
}
