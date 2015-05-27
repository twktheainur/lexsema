package org.getalp.lexsema.wsd.method;

import cern.jet.random.tdouble.engine.DoubleMersenneTwister;
import cern.jet.random.tdouble.engine.DoubleRandomEngine;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.util.ValueScale;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

import java.util.ArrayList;
import java.util.List;

public class SimulatedAnnealing2 implements Disambiguator
{
    public static final double T0_THRESHOLD = 0.01;
    
    public static final int NUMBER_OF_CHANGES = 10;
    
    public double iterations = 1000;
    
    boolean changedSinceLast = false;
    
    private DoubleRandomEngine uniformGenerator = new DoubleMersenneTwister(1);

    private double T;
    
    private double T0 = -1;

    private double p0;
    
    private int convergenceThreshold;
    
    private double coolingRate;

    private Configuration configuration;
        
    private Configuration previousConfiguration;
    
    private ConfigurationScorer configurationScorer;

    private double delta;
    
    private double maxDelta;
    
    private double bestScore;
    
    private double prevScore;
    
    private double currentCycle;
    
    private int convergenceCycles;
    
    private int numberOfAcceptanceEvents = 0;    
    
    public SimulatedAnnealing2(double p0, double coolingRate, int convergenceThreshold, int iterations, ConfigurationScorer configurationScorer)
    {
        this.convergenceThreshold = convergenceThreshold;
        this.p0 = p0;
        this.configurationScorer = configurationScorer;
        this.coolingRate = coolingRate;
        this.iterations = iterations;
    }

    private double findT0(double avgDelta, double targetProbability)
    {
        int i = 2;
        double formula;
        double probability;
        double currentThreshold = T0_THRESHOLD;
        do
        {
            formula = avgDelta / Math.exp(1 - 1.0 / (i + (double) 2)) * Math.log(i + 2);
            probability = Math.exp(-avgDelta / formula);
            if (probability > currentThreshold)
            {
                currentThreshold += T0_THRESHOLD;
            }
            i++;
        } while (probability <= targetProbability);
        return formula;
    }

    private void initialize(Document document) 
    {
        configuration = new ConfidenceConfiguration(document, ConfidenceConfiguration.InitializationType.RANDOM);
        maxDelta = delta;
        if (T0 < 0) 
        {
            initialEvaluation(document);
            T0 = findT0(delta, p0);
        }
        T = T0;
        currentCycle = 0;
        convergenceCycles = 0;
        bestScore = 0;
    }

    private double calculateT(double T0, double cycle) 
    {
        return T0 * Math.pow(coolingRate, cycle);
    }

    private void initialEvaluation(Document document)
    {
        List<Double> scores = new ArrayList<>();
        double sum;
        sum = 0;
        for (int i = 0; i < iterations; i++) 
        {
            double score = configurationScorer.computeScore(document, makeRandomChange(configuration, document, NUMBER_OF_CHANGES, uniformGenerator));
            scores.add(score);
        }

        prevScore = scores.get(0);
        double sumDelta = 0;
        for (double score : scores) 
        {
            sum += score;
            sumDelta += Math.abs(score - prevScore);
        }
        double currScore = sum / scores.size();

        sumDelta /= scores.size() - 1;
        bestScore = currScore;
        prevScore = currScore;
        delta = sumDelta;

        bestScore = 0;
        prevScore = 0;
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
        return disambiguate(document, new ConfidenceConfiguration(document));
    }
    
    public Configuration disambiguate(Document document, Configuration c) 
    {
        initialize(document);
        while (evaluate()) 
        {
            System.out.println(String.format("[Cycle %.2f] [T=%.2f] [Convergence: %d/%d] [Best: %f]", currentCycle, T, convergenceCycles, convergenceThreshold, bestScore));
            changedSinceLast = false;
            for (int j = 0; j < iterations; j++)
            {
                anneal(document, j);
            }
        }
        return configuration;
    }

    protected void anneal(Document document, int cycleNumber)
    {
        double score;

        Configuration cp = makeRandomChange(configuration, document, 1, uniformGenerator);
        score = configurationScorer.computeScore(document, cp);

        delta = prevScore - score;
        if (delta < 0)
        {
            configuration = cp;
            prevScore = score;
            if (score >= bestScore)
            {
                bestScore = score;
            }
            changedSinceLast = true;
            numberOfAcceptanceEvents++;
        }
        else 
        {
            if (delta > maxDelta) 
            {
                maxDelta = delta;
            }
            double choice = uniformGenerator.raw();
            double prob = Math.exp(-delta / T);
            if (prob > choice) 
            {
                configuration = cp;
                prevScore = score;
                changedSinceLast = true;
                numberOfAcceptanceEvents++;
            }
        }
        if (numberOfAcceptanceEvents > 100) 
        {
            numberOfAcceptanceEvents = 0;
        }
    }

    private boolean evaluate() 
    {
        T = calculateT(T0, currentCycle);
        {
            numberOfAcceptanceEvents = 0;
        }
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
