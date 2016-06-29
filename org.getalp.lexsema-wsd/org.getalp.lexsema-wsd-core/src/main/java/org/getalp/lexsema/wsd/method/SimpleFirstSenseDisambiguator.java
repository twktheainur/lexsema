package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;

public class SimpleFirstSenseDisambiguator implements Disambiguator
{
    public Configuration disambiguate(Document document)
    {
        return new ContinuousConfiguration(document, 0);
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c)
    {
        return disambiguate(document);
    }

    @Override
    public void release()
    {
        
    }
}
