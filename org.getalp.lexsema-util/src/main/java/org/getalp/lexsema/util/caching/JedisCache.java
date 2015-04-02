package org.getalp.lexsema.util.caching;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.ref.WeakReference;


/**
 * Wrapper for Jedis
 */
class JedisCache implements Cache {
    private Jedis cache;
    private WeakReference<JedisPool> parentPool;

    public JedisCache(Jedis cache, JedisPool parentPool) {
        this.cache = cache;
        this.parentPool = new WeakReference<>(parentPool);
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    @Override
    public Boolean exists(String key) {
        return cache.exists(key);
    }

    @Override
    public Long del(String key) {
        return cache.del(key);
    }

    @Override
    public String set(String key, String value) {
        return cache.set(key, value);
    }

    @Override
    public Long expire(String key, int seconds) {
        return cache.expire(key, seconds);
    }

    @Override
    public void close() {
        if (parentPool.get() != null) {
            //noinspection ConstantConditions
            parentPool.get().returnResource(cache);
        }
    }
}
