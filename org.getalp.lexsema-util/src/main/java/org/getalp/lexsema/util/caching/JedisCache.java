package org.getalp.lexsema.util.caching;


import redis.clients.jedis.Jedis;


/**
 * Wrapper for Jedis
 */
class JedisCache implements Cache {
    Jedis cache;

    public JedisCache(Jedis cache) {
        this.cache = cache;
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
        cache.close();
    }
}
