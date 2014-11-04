package org.getalp.disambiguation.method;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;

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
