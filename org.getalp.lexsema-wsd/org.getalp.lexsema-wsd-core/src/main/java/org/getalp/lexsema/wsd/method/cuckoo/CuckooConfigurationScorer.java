package org.getalp.lexsema.wsd.method.cuckoo;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class CuckooConfigurationScorer implements CuckooSolutionScorer
{
    private ConfigurationScorer scorer;
    
    private Document document;
    
    public CuckooConfigurationScorer(ConfigurationScorer scorer, Document document)
    {
        this.scorer = scorer;
        this.document = document;
    }

    public double computeScore(CuckooSolution configuration)
    {
        return scorer.computeScore(document, (Configuration) configuration);
    }

    public void release()
    {
        scorer.release();
    }
}
