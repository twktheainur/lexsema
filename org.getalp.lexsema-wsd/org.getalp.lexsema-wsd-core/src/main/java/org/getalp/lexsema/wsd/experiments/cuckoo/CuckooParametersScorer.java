package org.getalp.lexsema.wsd.experiments.cuckoo;

import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.CuckooSearch;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class CuckooParametersScorer
{
    private ConfigurationScorer scorer; 
    
    private TextLoader dl;
    
    private int iterationsOutside;
    
    private int iterationsInside;
    
    public CuckooParametersScorer(ConfigurationScorer scorer, TextLoader dl, int iterationsOutside, int iterationsInside)
    {
        this.scorer = scorer;
        this.dl = dl;
        this.iterationsOutside = iterationsOutside;
        this.iterationsInside = iterationsInside;
    }
    
    public double computeScore(CuckooParameters params)
    {
        double res = 0;
        for (int i = 0 ; i < iterationsOutside ; i++)
        {
            Disambiguator cuckooDisambiguator = new CuckooSearch(iterationsInside, 
                    params.levyScale.currentValue, 
                    params.nestsNumber.currentValue, 
                    (int)(params.destroyedNests.currentValue * params.nestsNumber.currentValue), 
                    scorer);
            double tmpres = 0;
            int nbTexts = 0;
            for (Document d : dl)
            {
                Configuration c = cuckooDisambiguator.disambiguate(d);
                tmpres += scorer.computeScore(d, c);
                nbTexts++;
            }
            cuckooDisambiguator.release();
            res += tmpres / ((double) nbTexts);
        }
        return res / ((double) iterationsOutside);
    }
}
