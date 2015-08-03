package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.evaluation.GoldStandard;

public interface Disambiguator {
    public Configuration disambiguate(Document document);

    public Configuration disambiguate(Document document, Configuration c);

    public void release();
}
