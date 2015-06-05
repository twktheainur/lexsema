package org.getalp.lexsema.wsd.parameters.bat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.getalp.lexsema.io.document.TextLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.BatAlgorithm;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.IterationStopCondition;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class BatParametersScorer implements CuckooSolutionScorer
{
    private ConfigurationScorer scorer; 
    
    private TextLoader dl;
    
    private int iterationsOutside;
    
    private StopCondition stopCondition;
    
    private ExecutorService threadPool;

    public BatParametersScorer(ConfigurationScorer scorer, TextLoader dl, int iterationsOutside, int iterationsInside)
    {
        this(scorer, dl, iterationsOutside, new IterationStopCondition(iterationsInside));
    }
    
    public BatParametersScorer(ConfigurationScorer scorer, TextLoader dl, int iterationsOutside, StopCondition stopCondition)
    {
        this.scorer = scorer;
        this.dl = dl;
        this.iterationsOutside = iterationsOutside;
        this.stopCondition = stopCondition;
        int nbThreads = Runtime.getRuntime().availableProcessors();
        threadPool = Executors.newFixedThreadPool(nbThreads);
    }
    
    public double computeScore(BatParameters params)
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
        return computeScore((BatParameters) configuration);
    }
    
    private class IntermediateScorer implements Callable<Double>
    {
        private BatParameters params;
        
        public IntermediateScorer(BatParameters params)
        {
            this.params = params;
        }

        public Double call() throws Exception
        {
            Disambiguator batDisambiguator = new BatAlgorithm(
                    stopCondition, 
                    (int) params.batsNumber.currentValue, 
                    params.minFrequency.currentValue,
                    params.maxFrequency.currentValue,
                    params.minLoudness.currentValue,
                    params.maxLoudness.currentValue,
                    params.alpha.currentValue,
                    params.gamma.currentValue,
                    scorer, false);
            double tmpres = 0;
            int nbTexts = 0;
            for (Document d : dl)
            {
                Configuration c = batDisambiguator.disambiguate(d);
                tmpres += scorer.computeScore(d, c);
                nbTexts++;
            }
            batDisambiguator.release();
            return (tmpres / ((double) nbTexts));
        }
    }
    
    public void finalize()
    {
        threadPool.shutdown();
    }
}
