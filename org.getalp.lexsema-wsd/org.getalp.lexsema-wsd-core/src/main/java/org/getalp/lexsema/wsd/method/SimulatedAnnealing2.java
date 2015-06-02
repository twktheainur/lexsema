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

    private int convergenceThreshold;
    
    private double iterations;

    private ConfigurationScorer configurationScorer;

    private boolean changedSinceLast;
    
    private double T;
    
    private Configuration configuration;
        
    private Configuration previousConfiguration;
    
    private double bestScore;
    
    private double prevScore;
    
    private int currentCycle;
    
    private int convergenceCycles;
    
    public SimulatedAnnealing2(double coolingRate, int convergenceThreshold, int iterations, ConfigurationScorer configurationScorer)
    {
        this.T0 = 200;
        this.coolingRate = coolingRate;
        this.convergenceThreshold = convergenceThreshold;
        this.iterations = iterations;
        this.configurationScorer = configurationScorer;
    }

    private void initialize(Document document) 
    {
        configuration = new ConfidenceConfiguration(document, ConfidenceConfiguration.InitializationType.RANDOM);
        T = T0;
        currentCycle = 0;
        convergenceCycles = 0;
        bestScore = 0;
        changedSinceLast = false;
    }

    private double calculateT(double T0, double cycle) 
    {
        return T0 * Math.pow(coolingRate, cycle);
    }

    private int nextRandomNatural(DoubleRandomEngine randomEngine, int max) 
    {
        return (int) ValueScale.scaleValue(randomEngine.raw(), 0d, 1d, 0, max);
    }

    private Configuration makeRandomChange(Configuration source, Document document, int numberOfChanges, DoubleRandomEngine gu) {
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
        initialize(document);
        while (evaluate()) 
        {
            System.out.println("[Cycle " + currentCycle + "] [T=" + T + "] [Convergence: " + convergenceCycles + "/" + convergenceThreshold + "] [Best: " + bestScore + "]");
            changedSinceLast = false;
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
            changedSinceLast = true;
        }
        else 
        {
            double choice = uniformGenerator.raw();
            double prob = Math.exp(-delta / T);
            if (prob > choice) 
            {
                configuration = cp;
                prevScore = score;
                changedSinceLast = true;
            }
        }
    }

    private boolean evaluate() 
    {
        T = calculateT(T0, currentCycle);
        if (convergenceCycles >= convergenceThreshold && configuration.equals(previousConfiguration)) 
        {
            return false;
        } 
        else if (!changedSinceLast) 
        {
            convergenceCycles++;
        } 
        else 
        {
            convergenceCycles = 0;
        }
        previousConfiguration = configuration;
        currentCycle++;
        return true;
    }

    public void release() 
    {
        configurationScorer.release();
    }
}
