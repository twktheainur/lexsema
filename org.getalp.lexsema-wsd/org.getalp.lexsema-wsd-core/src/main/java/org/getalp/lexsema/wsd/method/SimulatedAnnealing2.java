package org.getalp.lexsema.wsd.method;

import cern.jet.random.tdouble.engine.DoubleMersenneTwister;
import cern.jet.random.tdouble.engine.DoubleRandomEngine;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.util.ValueScale;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class SimulatedAnnealing2 implements Disambiguator
{
    private static DoubleRandomEngine uniformGenerator = new DoubleMersenneTwister(1);

    private double T0;

    private double coolingRate;

    private double iterations;

    private ConfigurationScorer configurationScorer;

    private double T;
    
    private Configuration configuration;
        
    private double bestScore;
    
    private double prevScore;
    
    private int currentCycle;
    
    private StopCondition stopCondition;
    
    private boolean verbose;
    
    public SimulatedAnnealing2(StopCondition stopCondition, double T0, double coolingRate, int iterations, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this.stopCondition = stopCondition;
        this.T0 = T0;
        this.coolingRate = coolingRate;
        this.iterations = iterations;
        this.configurationScorer = configurationScorer;
        this.verbose = verbose;
    }

    private void initialize(Document document) 
    {
        configuration = new ConfidenceConfiguration(document, ConfidenceConfiguration.InitializationType.RANDOM);
        T = T0;
        currentCycle = 0;
        bestScore = 0;
    }

    private double calculateT(double T0, double cycle) 
    {
        return T0 * Math.pow(coolingRate, cycle);
    }

    private int nextRandomNatural(DoubleRandomEngine randomEngine, int max) 
    {
        return (int) ValueScale.scaleValue(randomEngine.raw(), 0d, 1d, 0, max);
    }

    private Configuration makeRandomChange(Configuration source, Document document, int numberOfChanges, DoubleRandomEngine gu) 
    {
        Configuration newConfiguration = new ConfidenceConfiguration((ConfidenceConfiguration) source);

        for (int i = 0; i < numberOfChanges; i++) 
        {
            int changeIndex = nextRandomNatural(gu, source.size());
            int numberOfSenses = document.getSenses(changeIndex).size();
            int newIndex = nextRandomNatural(gu, numberOfSenses);
            newConfiguration.setSense(changeIndex, newIndex);
        }
        return newConfiguration;
    }

    public Configuration disambiguate(Document document) 
    {
        stopCondition.reset();
        initialize(document);
        while (evaluate()) 
        {
            if (verbose)
            {
                System.out.println("[Cycle " + currentCycle + "] [T=" + T + "] [Best: " + bestScore + "]");
            }
            for (int j = 0; j < iterations; j++)
            {
                anneal(document, j);
            }
        }
        return configuration;
    }
    
    public Configuration disambiguate(Document document, Configuration c) 
    {
        return disambiguate(document);
    }

    private void anneal(Document document, int cycleNumber)
    {
        Configuration cp = makeRandomChange(configuration, document, 1, uniformGenerator);
        double score = configurationScorer.computeScore(document, cp);

        double delta = prevScore - score;
        if (delta < 0)
        {
            configuration = cp;
            prevScore = score;
            if (score >= bestScore)
            {
                bestScore = score;
            }
        }
        else 
        {
            double choice = uniformGenerator.raw();
            double prob = Math.exp(-delta / T);
            if (prob > choice) 
            {
                configuration = cp;
                prevScore = score;
            }
        }
    }

    private boolean evaluate() 
    {
        T = calculateT(T0, currentCycle);
        currentCycle++;
        stopCondition.increment();
        return !stopCondition.stop();
    }

    public void release() 
    {
        configurationScorer.release();
    }
}
