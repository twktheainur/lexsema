package org.getalp.lexsema.wsd.parameters.cuckoo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.IterationStopCondition;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.method.cuckoo.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class CuckooParametersScorer implements CuckooSolutionScorer
{
    private ConfigurationScorer scorer; 
    
    private TextLoader dl;
    
    private int iterationsOutside;
    
    private StopCondition stopCondition;

    private ExecutorService threadPool;

    public CuckooParametersScorer(ConfigurationScorer scorer, TextLoader dl, int iterationsOutside, int iterationsInside)
    {
        this(scorer, dl, iterationsOutside, new IterationStopCondition(iterationsInside));
    }
    
    public CuckooParametersScorer(ConfigurationScorer scorer, TextLoader dl, int iterationsOutside, StopCondition stopCondition)
    {
        this.scorer = scorer;
        this.dl = dl;
        this.iterationsOutside = iterationsOutside;
        this.stopCondition = stopCondition;
        int nbThreads = Runtime.getRuntime().availableProcessors();
        threadPool = Executors.newFixedThreadPool(nbThreads);
    }
    
    public double computeScore(CuckooParameters params)
    {
        ArrayList<IntermediateScorer> scorers = new ArrayList<IntermediateScorer>();
        for (int i = 0 ; i < iterationsOutside ; i++)
        {
            scorers.add(new IntermediateScorer(params));
        }

        double res = 0;
        try
        {
            List<Future<Double>> intermediateScores = threadPool.invokeAll(scorers);
            for (Future<Double> intermediateScore : intermediateScores)
            {
                res += intermediateScore.get();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return res / ((double) iterationsOutside);
    }

    public double computeScore(CuckooSolution configuration)
    {
        return computeScore((CuckooParameters) configuration);
    }

    private class IntermediateScorer implements Callable<Double>
    {
        private CuckooParameters params;
        
        public IntermediateScorer(CuckooParameters params)
        {
            this.params = params;
        }

        public Double call() throws Exception
        {
            Disambiguator cuckooDisambiguator = new CuckooSearchDisambiguator(stopCondition, 
                    params.levyScale.currentValue, 
                    (int)params.nestsNumber.currentValue, 
                    (int)params.destroyedNests.currentValue, 
                    scorer, false);
            double tmpres = 0;
            int nbTexts = 0;
            for (Document d : dl)
            {
                Configuration c = cuckooDisambiguator.disambiguate(d);
                tmpres += scorer.computeScore(d, c);
                nbTexts++;
            }
            cuckooDisambiguator.release();
            return tmpres / ((double) nbTexts);
        }
    }
    
    public void finalize()
    {
        threadPool.shutdown();
    }
}
