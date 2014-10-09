package org.getalp.disambiguation.method;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;

/**
 * Created by tchechem on 9/16/14.
 */
public interface Disambiguator {
    public Configuration disambiguate(Document document);
}
