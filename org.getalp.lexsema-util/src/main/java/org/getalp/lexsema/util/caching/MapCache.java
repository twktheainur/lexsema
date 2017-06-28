package org.getalp.lexsema.util.caching;

import java.util.HashMap;
import java.util.Map;


public class MapCache implements Cache {
    private final Map<String, String> map = new HashMap<>();

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public Boolean exists(String key) {
        return map.containsKey(key);
    }

    @Override
    public Long del(String key) {
        String ret;
        ret = map.remove(key);
        if (ret == null) {
            return 0L;
        } else {
            return 1L;
        }
    }

    @Override
    public String set(String key, String value) {
        return map.put(key, value);
    }

    @Override
    public Long expire(String key, int seconds) {
        return 0L;
    }

    @Override
    public void close() {
        map.clear();
    }
}
