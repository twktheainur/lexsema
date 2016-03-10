package org.getalp.lexsema.translation;

import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.dataitems.Pair;
import org.getalp.lexsema.util.dataitems.PairImpl;
import org.getalp.lexsema.util.rest.RestfulQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLConnection;
import java.util.*;

public class GoogleWebTranslator implements Translator {
    private static Logger logger = LoggerFactory.getLogger(GoogleWebTranslator.class);

    public GoogleWebTranslator() {
    }

    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {

        //ry
        //https://translate.google.com/translate_a/single?client=t&sl=en&tl=fr&hl=en&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&source=btn&ssel=0&tsel=0&kc=0&tk=445256.59283&q=Bonjour%2C+je+m%27appelle+John

        List<Pair<String,String>> parameters = new ArrayList<>();
        parameters.add(new PairImpl<>("client", "t"));
        parameters.add(new PairImpl<>("sl", sourceLanguage.getISO2Code()));
        parameters.add(new PairImpl<>("tl", targetLanguage.getISO2Code()));
        parameters.add(new PairImpl<>("hl", "en"));
        parameters.add(new PairImpl<>("dt", "at"));
        parameters.add(new PairImpl<>("dt","bd"));
        parameters.add(new PairImpl<>("dt", "ex"));
        parameters.add(new PairImpl<>("dt", "ld"));
        parameters.add(new PairImpl<>("dt", "md"));
        parameters.add(new PairImpl<>("dt", "qca"));
        parameters.add(new PairImpl<>("dt", "rw"));
        parameters.add(new PairImpl<>("dt", "rm"));
        parameters.add(new PairImpl<>("dt", "ss"));
        parameters.add(new PairImpl<>("dt", "t"));
        parameters.add(new PairImpl<>("ie", "UTF-8"));
        parameters.add(new PairImpl<>("oe", "UTF-8"));
        parameters.add(new PairImpl<>("source", "btn"));
        parameters.add(new PairImpl<>("ssel", "0"));
        parameters.add(new PairImpl<>("tsel", "0"));
        parameters.add(new PairImpl<>("kc", "0"));
        parameters.add(new PairImpl<>("tk", "445256.59283"));
        parameters.add(new PairImpl<>("q",source));


        /*
client=t
sl=en
tl=fr
hl=fr
dt=at
dt=bd
dt=ex
dt=ld
dt=md
dt=qca
dt=rw
dt=rm
dt=ss
dt=t
ie=UTF-8
oe=UTF-8
source=btn
ssel=0
tsel=0
kc=0
tk=445256.59283
q=Hello%20there!%20Hell%20Hell

         */
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
