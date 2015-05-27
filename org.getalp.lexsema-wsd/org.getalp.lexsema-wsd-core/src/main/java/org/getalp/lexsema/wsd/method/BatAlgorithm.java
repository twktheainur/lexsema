package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

import java.util.Random;

public class BatAlgorithm implements Disambiguator
{
    private static final Random random = new Random();
    
    private int iterationsNumber;

    private int batsNumber;

    private double minFrequency;

    private double maxFrequency;

    private double minLoudness;

    private double maxLoudness;

    private double minRate;

    private double maxRate;

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
            return score;
        }
                
    }
    
    public BatAlgorithm(int iterationsNumber, int batsNumber, double minFrequency, double maxFrequency, 
                        double minLoudness, double maxLoudness, double minRate, double MaxRate, 
                        double alpha, double gamma, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this.iterationsNumber = iterationsNumber;
        this.batsNumber = batsNumber;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
        this.minLoudness = minLoudness;
        this.maxLoudness= maxLoudness;
        this.minRate = minRate;
        this.maxRate = MaxRate;
        this.alpha = alpha;
        this.gamma = gamma;
        this.configurationScorer = configurationScorer;
        bats = new Bat[batsNumber];
        this.verbose = verbose;
    }

    public Configuration disambiguate(Document document)
    {
        currentDocument = document;
        dimension = document.size();
        
        for (int i = 0 ; i < batsNumber ; ++i)
        {
            bats[i] = new Bat();
        }

        updateBestBat();

        for (int currentIteration = 0 ; currentIteration < iterationsNumber ; currentIteration++)
        {
            int progress = (int)(((double) currentIteration / (double) iterationsNumber) * 10000);
            if (verbose) System.out.println("Bat progress : " + (double)progress / 100.0 + "%");

            for (Bat currentBat : bats)
            {
                ContinuousConfiguration previousPosition = currentBat.position.clone();
                int previousVelocity = currentBat.velocity;
                double previousScore = currentBat.score;

                currentBat.frequency = randomDoubleInRange(minFrequency, maxFrequency);
                
                currentBat.velocity = 0;
                for (int i = 0 ; i < dimension ; i++)
                {
                    if (currentBat.position.getAssignment(i) != bestBat.position.getAssignment(i))
                    {
                        currentBat.velocity++;
                    }
                }
                currentBat.velocity *= currentBat.frequency;
                currentBat.position.makeRandomChanges(currentBat.velocity);
                
                if (currentBat.rate < randomDoubleInRange(minRate, maxRate))
                {
                    currentBat.position = bestBat.position.clone();
                    currentBat.position.makeRandomChanges((int) (random.nextGaussian() * getAverageLoudness()));
                }
                
                if (currentBat.loudness > randomDoubleInRange(minLoudness, maxLoudness) &&
                    currentBat.recomputeScore() > previousScore)
                {
                    currentBat.loudness *= alpha;
                    currentBat.rate = currentBat.initialRate * (1 - Math.exp(-gamma * currentIteration));
                    if (currentBat.score > bestBat.score)
                    {
                        bestBat = currentBat;
                    }
                }
                else
                {
                    currentBat.position = previousPosition;
                    currentBat.velocity = previousVelocity;
                    currentBat.score = previousScore;
                }
            }
            
            if (verbose) System.out.println("Current best : " + bestBat.score);
        }

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
