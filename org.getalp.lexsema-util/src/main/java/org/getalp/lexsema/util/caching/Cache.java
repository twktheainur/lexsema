package org.getalp.lexsema.util.caching;


public interface Cache {
    String get(final String key);

    Boolean exists(final String key);

    Long del(String key);

    String set(final String key, String value);

    Long expire(final String key, final int seconds);

    void close();
}
