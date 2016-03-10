package org.getalp.lexsema.translation;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.dataitems.Pair;
import org.getalp.lexsema.util.dataitems.PairImpl;
import org.getalp.lexsema.util.rest.RestfulQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApertiumWebTranslator implements Translator {
    private static final Logger logger = LoggerFactory.getLogger(ApertiumWebTranslator.class);

    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {
        List<Pair<String,String>> parameters = new ArrayList<>();
        parameters.add(new PairImpl<>("langpair", String.format("%s|%s", sourceLanguage.getISO2Code(), targetLanguage.getISO2Code())));
        parameters.add(new PairImpl<>("q", source));
        try {
            URLConnection urlConnection = RestfulQuery.restfulQuery("https://www.apertium.org/apy/translate",parameters);
            String response = RestfulQuery.getRequestOutput(urlConnection);
            JsonObject object = JSON.parse(response);
            return object.get("responseData").getAsObject().get("translatedText").toString();
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage());
        }
        return "";
    }

    @Override   
    public void close() {

    }

}
