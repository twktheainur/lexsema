package org.getalp.lexsema.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class JedisCachePool {

    private static JedisPool jedisPool = null;

    private JedisCachePool() {
    }

    public static Jedis getResource() {
        if (jedisPool == null) {
            jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
        }
        return jedisPool.getResource();
    }
}
