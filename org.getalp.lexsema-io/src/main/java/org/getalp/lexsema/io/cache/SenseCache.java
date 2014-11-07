package org.getalp.lexsema.io.cache;

import org.getalp.lexsema.io.LexicalEntry;
import org.getalp.lexsema.io.Sense;

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
