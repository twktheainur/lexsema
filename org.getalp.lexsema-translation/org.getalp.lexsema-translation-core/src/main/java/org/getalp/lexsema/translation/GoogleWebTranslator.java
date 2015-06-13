package org.getalp.lexsema.translation;

import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.rest.RestfulQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class GoogleWebTranslator implements Translator {
    private static Logger logger = LoggerFactory.getLogger(GoogleWebTranslator.class);

    public GoogleWebTranslator() {
    }

    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {
        //https://translate.google.com/translate_a/single?client=t&sl=en&tl=ru&hl=en&dt=t&q=Hello%20my%20friend
        Map<String,String> parameters = new HashMap<>();
        parameters.put("client","t");
        parameters.put("dt","t");
        parameters.put("sl", sourceLanguage.getISO2Code());
        parameters.put("tl", targetLanguage.getISO2Code());
        parameters.put("hl", "en");
        parameters.put("q",source);
        try {
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);

            //URLConnection urlConnection = RestfulQuery.restfulQuery("http://translate.google.com/", new HashMap<String, String>());
            //urlConnection.getContent();

            URLConnection urlConnection1 = RestfulQuery.restfulQuery("https://translate.google.com/translate_a/single",parameters);
            String response = RestfulQuery.getRequestOutput(urlConnection1);
            response = response.split("\"")[1];
            //logger.error(response);
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
