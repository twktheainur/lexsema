package org.getalp.lexsema.wsd.method;

import java.io.PrintWriter;
import java.util.Random;

import cern.jet.random.tdouble.engine.DoubleMersenneTwister;
import cern.jet.random.tdouble.engine.DoubleRandomEngine;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class SimulatedAnnealing2 implements Disambiguator
{
    public PrintWriter plotWriter = null;

    private static Random uniformGenerator = new Random();

    private double T0;

    private double coolingRate;

    private double iterations;

    private ConfigurationScorer configurationScorer;

    private double T;
    
    private ContinuousConfiguration configuration;
        
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
        configuration = new ContinuousConfiguration(document);
        T = T0;
        currentCycle = 0;
        bestScore = 0;
    }

    private double calculateT(double T0, double cycle) 
    {
        return T0 * Math.pow(coolingRate, cycle);
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
        if (plotWriter != null) plotWriter.flush();
        return configuration;
    }
    
    public Configuration disambiguate(Document document, Configuration c) 
    {
        return disambiguate(document);
    }

    private void anneal(Document document, int cycleNumber)
    {
        ContinuousConfiguration cp = configuration.clone();
        cp.makeRandomChange();
        double score = configurationScorer.computeScore(document, cp);
        stopCondition.incrementScorerCalls();

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
            double choice = uniformGenerator.nextDouble();
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
        if (plotWriter != null) plotWriter.println(stopCondition.getCurrent() + " " + bestScore);
        T = calculateT(T0, currentCycle);
        currentCycle++;
        stopCondition.incrementIterations();
        stopCondition.updateMilliseconds();
        return !stopCondition.stop();
    }

    public void release() 
    {
        configurationScorer.release();
    }
}
