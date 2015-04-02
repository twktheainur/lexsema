package org.getalp.lexsema.util.caching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.io.File.separator;

/**
 * Abstraction layer for a caching system cache pool
 */
public final class CachePool {

    public static final CacheProvider DEFAULT_CACHE_PROVIDER = CacheProvider.DUMMY;
    public final static String DEFAULT_HOST = "localhost";
    private final static CachePooledResourceProvider CACHE_POOLED_RESOURCE_PROVIDER = buildProvider();
    private static Logger logger = LoggerFactory.getLogger(CachePool.class);
    private static CacheProvider provider = DEFAULT_CACHE_PROVIDER;

    private static String host = DEFAULT_HOST;
    private static Integer port;


    private CachePool() {
    }

    private static void loadProperties() {
        final Properties properties = new Properties();
        try (InputStream props = CachePool.class.getResourceAsStream(separator + "lexsema_cache.properties")) {
            if (props != null) {
                properties.load(props);
                if (properties.containsKey("lexsema.cache.provider")) {
                    provider = CacheProvider.valueOf(properties.getProperty("lexsema.cache.provider"));
                    logger.info(String.format("[CONFIG] Loaded lexsema.cache.provider=%s", provider.toString()));
                }
                if (properties.containsKey("lexsema.cache.host")) {
                    host = properties.getProperty("lexsema.cache.host");
                    logger.info(String.format("[CONFIG] Loaded lexsema.cache.host=%s", host));
                }
                if (properties.containsKey("lexsema.cache.port")) {
                    port = Integer.valueOf(properties.getProperty("lexsema.cache.port"));
                    logger.info(String.format("[CONFIG] Loaded lexsema.cache.port=%d", port));
                }
            } else {
                logger.info("No lexsema_cache.properties in the classpath, using default configuration.");
            }
        } catch (IOException e) {
            logger.info("No lexsema_cache.properties in the classpath, using default configuration.");
        }
    }

    private static CachePooledResourceProvider buildProvider() {
        loadProperties();
        CachePooledResourceProvider resourceProvider;
        switch (provider) {
            case JEDIS:
                if (port != null) {
                    resourceProvider = new JedisCachePoolProvider(host, port);
                } else {
                    resourceProvider = new JedisCachePoolProvider(host);
                }
                break;
            case DUMMY:
            default:
                resourceProvider = new DummyCachePoolProvider();
                break;
        }
        return resourceProvider;
    }

    public synchronized static Cache getResource() {
        return CACHE_POOLED_RESOURCE_PROVIDER.getResource();
    }

}
