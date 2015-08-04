package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.method.StopCondition;
import java.io.PrintWriter;
import java.util.Random;

public class BatAlgorithmDisambiguator implements Disambiguator
{
    public PrintWriter plotWriter = null;
    
    private static final Random random = new Random();

    private static final double minRate = 0;

    private static final double maxRate = 1;

    private StopCondition stopCondition;

    private int currentIteration;
    
    private int batsNumber;

    private double minFrequency;

    private double maxFrequency;

    private double minLoudness;

    private double maxLoudness;

    private double alpha;

    private double gamma;

    private ConfigurationScorer configurationScorer;

    private Document currentDocument;

    private int dimension;

    private Bat[] bats;

    private Bat bestBat;
    
    private boolean verbose;
    
    private class Bat
    {
        private ContinuousConfiguration position;
        private int velocity;
        private double frequency;
        private double initialRate;
        private double rate;
        private double loudness;
        private double score;

        public Bat()
        {
            position = new ContinuousConfiguration(currentDocument);
            velocity = 0;
            frequency = randomDoubleInRange(minFrequency, maxFrequency);
            initialRate = randomDoubleInRange(minRate, maxRate);
            rate = initialRate;
            loudness = randomDoubleInRange(minLoudness, maxLoudness);
            recomputeScore();
        }

        public double recomputeScore()
        {
            score = configurationScorer.computeScore(currentDocument, position);
            stopCondition.incrementScorerCalls();
            return score;
        }
    }
    
    public BatAlgorithmDisambiguator(int iterationsNumber, int batsNumber, double minFrequency, double maxFrequency, 
                        double minLoudness, double maxLoudness, 
                        double alpha, double gamma, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterationsNumber), batsNumber, minFrequency, maxFrequency, 
             minLoudness, maxLoudness, 
             alpha, gamma, configurationScorer, verbose);
    }

    public BatAlgorithmDisambiguator(StopCondition stopCondition, int batsNumber, double minFrequency, double maxFrequency, 
                        double minLoudness, double maxLoudness, 
                        double alpha, double gamma, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this.stopCondition = stopCondition;
        this.batsNumber = batsNumber;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
        this.minLoudness = minLoudness;
        this.maxLoudness= maxLoudness;
        this.alpha = alpha;
        this.gamma = gamma;
        this.configurationScorer = configurationScorer;
        bats = new Bat[batsNumber];
        this.verbose = verbose;
    }

    public Configuration disambiguate(Document document)
    {
        stopCondition.reset();
        currentDocument = document;
        dimension = document.size();
        currentIteration = 0;
        int nbBatsFinished = 0;
        
        for (int i = 0 ; i < batsNumber ; ++i)
        {
            bats[i] = new Bat();
        }

        updateBestBat();

        while (!stopCondition.stop() && nbBatsFinished < batsNumber)
        {
            if (plotWriter != null) plotWriter.println(stopCondition.getCurrent() + " " + bestBat.score);
            int progress = (int)(stopCondition.getProgressPercentage() * 100);

            for (Bat currentBat : bats)
            {
                ContinuousConfiguration previousPosition = currentBat.position.clone();
                int previousVelocity = currentBat.velocity;
                double previousScore = currentBat.score;

                if (currentBat.rate < randomDoubleInRange(minRate, maxRate))
                {
                    currentBat.position = bestBat.position.clone();
                    currentBat.position.makeRandomChanges((int) getAverageLoudness());
                }
                else
                {
                    currentBat.frequency = randomDoubleInRange(minFrequency, maxFrequency);

                    for (int i = 0 ; i < dimension ; i++)
                    {
                        if (currentBat.position.getAssignment(i) != bestBat.position.getAssignment(i))
                        {
                            currentBat.velocity++;
                        }
                    }
                    currentBat.velocity *= currentBat.frequency;
                    currentBat.position.makeRandomChanges(currentBat.velocity);
                }

                if (currentBat.loudness >= randomDoubleInRange(minLoudness, maxLoudness) &&
                    currentBat.recomputeScore() > bestBat.score)
                {
                    currentBat.loudness *= alpha;
                    if (currentBat.loudness < minLoudness) nbBatsFinished++;
                    currentBat.rate = currentBat.initialRate * (1 - Math.exp(-gamma * currentIteration));
                    bestBat = currentBat;
                }
                else
                {
                    currentBat.position = previousPosition;
                    currentBat.velocity = previousVelocity;
                    currentBat.score = previousScore;
                }
            }
            
            stopCondition.incrementIterations();
            stopCondition.updateMilliseconds();
            currentIteration++;

            if (verbose) System.out.println("Bat progress : " + (double)progress / 100.0 + "% - " + 
                                            "Current best : " + bestBat.score);
        }
        if (plotWriter != null) plotWriter.flush();
        return bestBat.position;
    }

    public Configuration disambiguate(Document document, Configuration c)
    {
        return disambiguate(document);
    }

    public void release()
    {
        configurationScorer.release();
    }

    private double randomDoubleInRange(double min, double max)
    {
        return min + (max - min) * random.nextDouble();
    }

    private void updateBestBat()
    {
        double bestScore = Double.MIN_VALUE;
        for (Bat currentBat : bats)
        {
            double currentScore = currentBat.score;
            if (currentScore > bestScore)
            {
                bestScore = currentScore;
                bestBat = currentBat;
            }
        }
    }

    private double getAverageLoudness()
    {
        double average = 0;
        for (Bat currentBat : bats) average += currentBat.loudness;
        return (average / (double)bats.length);
    }
}
