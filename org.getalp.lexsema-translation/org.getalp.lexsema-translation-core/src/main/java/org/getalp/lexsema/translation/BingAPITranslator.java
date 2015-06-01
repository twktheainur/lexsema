package org.getalp.lexsema.translation;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BingAPITranslator implements Translator {

    Logger logger = LoggerFactory.getLogger(BingAPITranslator.class);

    public BingAPITranslator(String translatorId, String translatorPass) {
        Translate.setClientId(translatorId);
        Translate.setClientSecret(translatorPass);
    }

    @Override
    public String translate(String source, org.getalp.lexsema.util.Language sourceLanguage, org.getalp.lexsema.util.Language targetLanguage) {
        String translatedText = null;
        try {
            Language s = Language.fromString(sourceLanguage.getISO2Code());
            Language t = Language.fromString(targetLanguage.getISO2Code());

            if (s != null && t != null) {
                translatedText = Translate.execute(source, s, t);
            }
        } catch (Exception e) {
            logger.error(String.format("Translation error occurred: %s", e.getLocalizedMessage()));
        }

        return translatedText;
    }

    @Override
    public void close() {
    }
}
