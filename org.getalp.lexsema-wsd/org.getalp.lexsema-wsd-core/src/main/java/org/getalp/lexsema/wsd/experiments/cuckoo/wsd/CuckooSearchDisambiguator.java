package org.getalp.lexsema.wsd.experiments.cuckoo.wsd;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSearch;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class CuckooSearchDisambiguator implements Disambiguator
{
    private int iterations;
    
    private double levyScale;

    private int nestsNumber;
    
    private int destroyedNests;

    private ConfigurationScorer configurationScorer;

    public CuckooSearchDisambiguator(int iterations, double levyScale, int nestsNumber, int destroyedNests, ConfigurationScorer configurationScorer)
    {
        this.iterations = iterations;
        this.levyScale = levyScale;
        this.nestsNumber = nestsNumber;
        this.destroyedNests = destroyedNests;
        this.configurationScorer = configurationScorer;
    }

    public Configuration disambiguate(Document document)
    {
        CuckooConfigurationScorer scorer = new CuckooConfigurationScorer(configurationScorer, document);
        CuckooConfigurationFactory configurationFactory = new CuckooConfigurationFactory(document);
        CuckooSearch cuckoo = new CuckooSearch(iterations, levyScale, nestsNumber, destroyedNests, scorer, configurationFactory);
        return (Configuration) cuckoo.run();
    }

    public Configuration disambiguate(Document document, Configuration c)
    {
        return disambiguate(document);
    }
    
    public void release()
    {
        configurationScorer.release();
    }
}
