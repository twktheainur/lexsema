package org.getalp.lexsema.wsd.parameters.annealing;

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
import org.getalp.lexsema.wsd.method.SimulatedAnnealing2;
import org.getalp.lexsema.wsd.method.cuckoo.CuckooSearchDisambiguator;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionScorer;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class AnnealingParametersScorer implements CuckooSolutionScorer
{
    private ConfigurationScorer scorer; 
    
    private TextLoader dl;
    
    private int iterationsOutside;
    
    private int iterationsInside;

    private ExecutorService threadPool;

    public AnnealingParametersScorer(ConfigurationScorer scorer, TextLoader dl, int iterationsOutside, int iterationsInside)
    {
        this.scorer = scorer;
        this.dl = dl;
        this.iterationsOutside = iterationsOutside;
        this.iterationsInside = iterationsInside;
        int nbThreads = Runtime.getRuntime().availableProcessors();
        threadPool = Executors.newFixedThreadPool(nbThreads);
    }
    
    public double computeScore(AnnealingParameters params)
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
        return computeScore((AnnealingParameters) configuration);
    }

    private class IntermediateScorer implements Callable<Double>
    {
        private AnnealingParameters params;
        
        public IntermediateScorer(AnnealingParameters params)
        {
            this.params = params;
        }

        public Double call() throws Exception
        {
            Disambiguator annealingDisambiguator = new SimulatedAnnealing2(
                    params.coolingRate.currentValue, 
                    (int) params.convergenceThreshold.currentValue, 
                    (int) params.iterationsNumber.currentValue, 
                    scorer);
            double tmpres = 0;
            int nbTexts = 0;
            for (Document d : dl)
            {
                Configuration c = annealingDisambiguator.disambiguate(d);
                tmpres += scorer.computeScore(d, c);
                nbTexts++;
            }
            annealingDisambiguator.release();
            return tmpres / ((double) nbTexts);
        }
    }
    
    public void finalize()
    {
        threadPool.shutdown();
    }
}
