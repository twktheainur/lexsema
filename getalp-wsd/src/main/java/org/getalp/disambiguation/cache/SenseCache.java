package org.getalp.disambiguation.cache;

import org.getalp.disambiguation.LexicalEntry;
import org.getalp.disambiguation.Sense;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SenseCache {

    Map<LexicalEntry, List<Sense>> cache;

    private static SenseCache instance;

    public static SenseCache getInstance() {
        if (instance == null) {
            instance = new SenseCache();
        }
        return instance;
    }

    private SenseCache() {
        cache = new HashMap<>(1000000);
    }

    public List<Sense> getSenses(LexicalEntry w) {
        return cache.get(w);
    }

    public void addCache(LexicalEntry w, List<Sense> ls) {
        cache.put(w, ls);
    }
}
