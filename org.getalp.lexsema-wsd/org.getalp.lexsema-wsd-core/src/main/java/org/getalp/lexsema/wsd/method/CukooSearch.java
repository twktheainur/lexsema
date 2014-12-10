package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

/**
 * Created by tchechem on 10/24/14.
 */
public class CukooSearch implements Disambiguator {
    @Override
    public Configuration disambiguate(Document document) {
        return disambiguate(document, new Configuration(document));
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {
        return null;
    }


    @Override
    public void release() {

    }
}
