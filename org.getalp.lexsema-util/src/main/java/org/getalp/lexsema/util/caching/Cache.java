package org.getalp.lexsema.util.caching;


public interface Cache {
    public String get(final String key);

    public Boolean exists(final String key);

    public Long del(String key);

    public String set(final String key, String value);

    public Long expire(final String key, final int seconds);

    public void close();
}
