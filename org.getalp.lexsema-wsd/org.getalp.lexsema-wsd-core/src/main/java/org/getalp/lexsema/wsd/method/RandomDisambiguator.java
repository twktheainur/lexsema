package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;

public class RandomDisambiguator implements Disambiguator
{
    public Configuration disambiguate(Document document)
    {
        return new ContinuousConfiguration(document);
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
