package org.getalp.lexsema.util.caching;


public class DummyCache implements Cache {
    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public Boolean exists(String key) {
        return null;
    }

    @Override
    public Long del(String key) {
        return null;
    }

    @Override
    public String set(String key, String value) {
        return null;
    }

    @Override
    public Long expire(String key, int seconds) {
        return null;
    }

    @Override
    public void close() {

    }
}
