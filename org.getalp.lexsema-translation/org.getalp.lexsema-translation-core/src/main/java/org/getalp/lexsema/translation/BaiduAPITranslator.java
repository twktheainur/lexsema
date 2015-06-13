package org.getalp.lexsema.translation;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.rest.RestfulQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BaiduAPITranslator implements Translator {
    private static Logger logger = LoggerFactory.getLogger(BaiduAPITranslator.class);

    private String key;

    public BaiduAPITranslator(String key) {
        this.key = key;
    }

    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("client_id",key);
        parameters.put("from", sourceLanguage.getISO2Code());
        parameters.put("to", targetLanguage.getISO2Code());
        parameters.put("q",source);
        try {
            String response = RestfulQuery.restfulQuery("http://openapi.baidu.com/public/2.0/bmt/translate",parameters);
            JsonObject object = JSON.parse(response);
            response = object.get("trans_result").getAsArray().get(0).getAsObject().get("dst").getAsString().value();
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
