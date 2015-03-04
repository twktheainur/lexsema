package org.getalp.lexsema.ontolex.graph.storage;

import com.hp.hpl.jena.query.ResultSet;
import org.getalp.lexsema.ontolex.graph.store.QueryCache;

import java.util.HashMap;
import java.util.Map;


public class DefaultQueryCache implements QueryCache {

    public Map<String, ResultSet> cache;

    public DefaultQueryCache() {
        cache = new HashMap<>();
    }

    @Override
    public synchronized ResultSet retrieveResult(String query) {
        return cache.get(query);
    }

    @Override
    public synchronized void addResult(String query, ResultSet resultSet) {
        cache.put(query, resultSet);
    }

    @Override
    public synchronized boolean hasResult(String query) {
        return cache.containsKey(query);
    }

    @Override
    public synchronized void invalidateResult(String query) {
        cache.remove(query);
    }

    @Override
    public synchronized void invalidateAll() {
        cache.clear();
    }

    @Override
    public synchronized void purge() {
        cache.clear();
    }
}
