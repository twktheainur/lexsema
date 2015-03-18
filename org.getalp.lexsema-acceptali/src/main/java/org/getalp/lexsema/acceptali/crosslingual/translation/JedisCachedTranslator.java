package org.getalp.lexsema.acceptali.crosslingual.translation;

import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.util.JedisCachePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;


public class JedisCachedTranslator implements Translator {

    private Logger logger = LoggerFactory.getLogger(JedisCachedTranslator.class);

    private Translator translator;
    private String prefix;
    private Jedis jedis = JedisCachePool.getResource();

    public JedisCachedTranslator(String prefix, Translator translator) {
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
