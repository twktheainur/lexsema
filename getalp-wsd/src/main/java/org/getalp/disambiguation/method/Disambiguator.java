package org.getalp.disambiguation.method;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;

public interface Disambiguator {
    public Configuration disambiguate(Document document);

    public Configuration disambiguate(Document document, Configuration c);

    public void release();
}
