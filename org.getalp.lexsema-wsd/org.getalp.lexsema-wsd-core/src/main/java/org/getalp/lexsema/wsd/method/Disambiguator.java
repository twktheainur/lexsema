package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.io.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;

public interface Disambiguator {
    public Configuration disambiguate(Document document);

    public Configuration disambiguate(Document document, Configuration c);

    public void release();
}
