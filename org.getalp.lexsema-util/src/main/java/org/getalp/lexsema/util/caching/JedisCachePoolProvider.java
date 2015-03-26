package org.getalp.lexsema.util.caching;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

final class JedisCachePoolProvider implements CachePooledResourceProvider {

    private JedisPool jedisPool = null;

    public JedisCachePoolProvider(String host) {
        jedisPool = new JedisPool(new JedisPoolConfig(), host);
    }

    public JedisCachePoolProvider(String host, int port) {
        jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
    }

    @Override
    public Cache getResource() {
        return new JedisCache(jedisPool.getResource());
    }
}
