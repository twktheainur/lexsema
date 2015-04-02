package org.getalp.lexsema.util.caching;

/**
 * A Dummy cache implementation
 */
public class DummyCachePoolProvider implements CachePooledResourceProvider {
    private static Cache cache = new DummyCache();

    @Override
    public Cache getResource() {
        return cache;
    }
}
