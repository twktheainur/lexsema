package org.getalp.lexsema.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class JedisCachePool {

    private static JedisPool jedisPool = null;

    private JedisCachePool() {
    }

    public static void setHost(String hostname) {
        setHost(hostname, -1);
    }

    public static void setHost(String hostname, int port) {
        if (jedisPool != null) {
            jedisPool.close();
        }
        if (port < 0) {
            jedisPool = new JedisPool(new JedisPoolConfig(), hostname);
        } else {
            jedisPool = new JedisPool(new JedisPoolConfig(), hostname, port);
        }

    }

    public static Jedis getResource() {
        if (jedisPool == null) {
            jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
        }
        return jedisPool.getResource();
    }
}
