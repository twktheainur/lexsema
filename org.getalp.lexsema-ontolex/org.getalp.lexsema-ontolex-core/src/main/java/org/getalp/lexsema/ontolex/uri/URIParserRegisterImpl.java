package org.getalp.lexsema.ontolex.uri;

import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.exceptions.NotRegisteredException;

import java.util.HashMap;
import java.util.Map;


public class URIParserRegisterImpl implements URIParserRegister {
    private Map<Class<? extends LexicalResourceEntity>, URIParser> factoryCache;

    @Override
    public void registerURIParser(Class<? extends LexicalResourceEntity> lrclass, URIParser uriParser) {
        if (factoryCache == null) {
            factoryCache = new HashMap<>();
        }
        if (!factoryCache.containsKey(lrclass)) {
            factoryCache.put(lrclass, uriParser);
        }
    }

    @Override
    public URIParser getFactory(Class<? extends LexicalResourceEntity> lrclass) throws NotRegisteredException {
        if (!factoryCache.containsKey(lrclass)) {
            throw new NotRegisteredException(lrclass);
        }
        return factoryCache.get(lrclass);
    }
}
