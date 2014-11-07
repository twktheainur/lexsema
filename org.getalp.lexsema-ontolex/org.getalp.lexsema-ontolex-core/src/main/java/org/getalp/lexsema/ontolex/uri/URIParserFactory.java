package org.getalp.lexsema.ontolex.uri;

import org.getalp.lexsema.ontolex.LexicalResource;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.exceptions.NotRegisteredException;

import java.util.HashMap;
import java.util.Map;


public abstract class URIParserFactory {
    private static Map<Class<? extends LexicalResource>, URIParserFactory> factoryCache;

    public static void registerFactory(Class<? extends LexicalResource> lrclass, URIParserFactory factory) {
        if (factoryCache == null) {
            factoryCache = new HashMap<>();
        }
        if (!factoryCache.containsKey(lrclass)) {
            factoryCache.put(lrclass, factory);
        }
    }

    public static URIParserFactory getFactory(Class<? extends LexicalResource> lrclass) throws NotRegisteredException {
        if (!factoryCache.containsKey(lrclass)) {
            throw new NotRegisteredException(lrclass);
        }
        return factoryCache.get(lrclass);
    }

    public abstract URIParser createURIParser(Class<? extends LexicalResourceEntity> pclass);
}
