package org.getalp.disambiguation.method;

import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.io.Document;

public interface Disambiguator {
    public Configuration disambiguate(Document document);

    public Configuration disambiguate(Document document, Configuration c);

    public void release();
}
