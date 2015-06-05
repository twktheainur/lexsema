package org.getalp.lexsema.wsd.parameters.genetic;

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
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionScorer;
import org.getalp.lexsema.wsd.method.genetic.GeneticAlgorithmDisambiguator;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class GeneticParametersScorer implements CuckooSolutionScorer
{
    private ConfigurationScorer scorer; 
    
    private TextLoader dl;
    
    private int iterationsOutside;
    
    private int iterationsInside;

    private ExecutorService threadPool;

    public GeneticParametersScorer(ConfigurationScorer scorer, TextLoader dl, int iterationsOutside, int iterationsInside)
    {
        this.scorer = scorer;
        this.dl = dl;
        this.iterationsOutside = iterationsOutside;
        this.iterationsInside = iterationsInside;
        int nbThreads = Runtime.getRuntime().availableProcessors();
        threadPool = Executors.newFixedThreadPool(nbThreads);
    }
    
    public double computeScore(GeneticParameters params)
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
        return computeScore((GeneticParameters) configuration);
    }

    private class IntermediateScorer implements Callable<Double>
    {
        private GeneticParameters params;
        
        public IntermediateScorer(GeneticParameters params)
        {
            this.params = params;
        }

        public Double call() throws Exception
        {
            Disambiguator geneticDisambiguator = new GeneticAlgorithmDisambiguator(
                    iterationsInside, 
                    (int) params.population.currentValue, 
                    params.crossoverRate.currentValue, 
                    params.mutationRate.currentValue, 
                    scorer);
            double tmpres = 0;
            int nbTexts = 0;
            for (Document d : dl)
            {
                Configuration c = geneticDisambiguator.disambiguate(d);
                tmpres += scorer.computeScore(d, c);
                nbTexts++;
            }
            geneticDisambiguator.release();
            return tmpres / ((double) nbTexts);
        }
    }
    
    public void finalize()
    {
        threadPool.shutdown();
    }
}
