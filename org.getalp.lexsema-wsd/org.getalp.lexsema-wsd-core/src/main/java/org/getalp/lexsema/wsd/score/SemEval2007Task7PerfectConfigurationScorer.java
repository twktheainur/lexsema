package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.evaluation.Semeval2007GoldStandard;
import org.getalp.lexsema.wsd.evaluation.StandardEvaluation;
import org.getalp.lexsema.wsd.evaluation.WSDResult;

public class SemEval2007Task7PerfectConfigurationScorer implements ConfigurationScorer
{
    private Semeval2007GoldStandard goldStandard;
    
    private StandardEvaluation evaluation;
    
    public SemEval2007Task7PerfectConfigurationScorer()
    {
        goldStandard = new Semeval2007GoldStandard();
        evaluation = new StandardEvaluation();
    }
    
    public double computeScore(Document document, Configuration configuration)
    {
        WSDResult result = evaluation.evaluate(goldStandard, configuration);
        return result.getPrecision();
    }

    public void release()
    {
        
    }
}
