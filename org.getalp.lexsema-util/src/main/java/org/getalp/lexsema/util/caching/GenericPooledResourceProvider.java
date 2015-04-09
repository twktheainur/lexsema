package org.getalp.lexsema.util.caching;

import java.util.HashMap;
import java.util.Map;


public class GenericPooledResourceProvider implements CachePooledResourceProvider {

    Map<String, Cache> caches = new HashMap<>();

    @Override
    public synchronized Cache getResource() {
        String key = Thread.currentThread().getName();
        if (!caches.containsKey(key)) {
            caches.put(key, new MapCache());
        }
        return caches.get(key);
    }
}
