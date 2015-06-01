package org.getalp.lexsema.translation;

import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.caching.Cache;
import org.getalp.lexsema.util.caching.CachePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CachedTranslator implements Translator {

    private Logger logger = LoggerFactory.getLogger(CachedTranslator.class);

    private Translator translator;
    private String prefix;
    private Cache jedis = CachePool.getResource();

    public CachedTranslator(String prefix, Translator translator) {
        this.prefix = prefix;
        this.translator = translator;

    }

    private String produceKey(String source, Language sourceLanguage, Language targetLanguage) {
        return String.format("%s_translation____%s____%s____%s", prefix, sourceLanguage.getISO2Code(), targetLanguage.getISO2Code(), source);
    }

    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {
        String translation = "";
        String key = produceKey(source, sourceLanguage, targetLanguage);
        if (!jedis.exists(key)) {
            translation = translator.translate(source, sourceLanguage, targetLanguage);
            jedis.set(key, translation);
        } else {
            translation = jedis.get(key);
        }
        return translation;
    }

    @Override
    public void close() {
        jedis.close();
    }
}
