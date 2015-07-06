package org.getalp.lexsema.translation;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.rest.RestfulQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class ApertiumWebTranslator implements Translator {
    private static Logger logger = LoggerFactory.getLogger(ApertiumWebTranslator.class);
    public ApertiumWebTranslator() {
    }
    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("langpair",String.format("%s|%s",sourceLanguage.getISO2Code(),targetLanguage.getISO2Code()));
        parameters.put("q",source);
        try {
            URLConnection urlConnection = RestfulQuery.restfulQuery("https://www.apertium.org/apy/translate",parameters);
            String response = RestfulQuery.getRequestOutput(urlConnection);
            JsonObject object = JSON.parse(response);
            response = object.get("responseData").getAsObject().get("translatedText").toString();
            return response;
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage());
        }
        return "";
    }

    @Override   
    public void close() {

    }

}
