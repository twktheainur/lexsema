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

    public BatAlgorithm(int iterationsNumber, int batsNumber, double minFrequency, double maxFrequency,
                        double minLoudness, double maxLoudness, double minRate, double MaxRate,
                        double alpha, double gamma, ConfigurationScorer configurationScorer)
    {
        this.iterationsNumber = iterationsNumber;
        this.batsNumber = batsNumber;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
        this.minLoudness = minLoudness;
        this.maxLoudness= maxLoudness;
        this.minRate = minRate;
        maxRate = MaxRate;
        this.alpha = alpha;
        this.gamma = gamma;
        this.configurationScorer = configurationScorer;
        bats = new Bat[batsNumber];
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
            int progress = (int) ((double) currentIteration / (double) iterationsNumber * 10000);
            System.out.println("Bat progress : " + (double)progress / 100.0 + "%");

            for (Bat currentBat : bats)
            {
                Integer[] previousPosition = currentBat.position.clone();
                Integer[] previousVelocity = currentBat.velocity.clone();
                double previousScore = currentBat.score;

                currentBat.frequency = randomDoubleInRange(minFrequency, maxFrequency);

                for (int i = 0 ; i < dimension ; i++)
                {
                    currentBat.velocity[i] += (int) ((currentBat.position[i] - bestBat.position[i]) * currentBat.frequency);
                    currentBat.position[i] += currentBat.velocity[i];
                }

                if (currentBat.rate < randomDoubleInRange(minRate, maxRate))
                {
                    for (int i = 0 ; i < dimension ; i++)
                    {
                        currentBat.position[i] = bestBat.position[i] + (int) random.nextGaussian();
                    }
                }

                currentBat.recomputeScore();

                if (currentBat.loudness > randomDoubleInRange(minLoudness, maxLoudness) &&
                    currentBat.score > previousScore)
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

            System.out.println("Current best : " + bestBat.score);
        }

        return bestBat.configuration;
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

    private class Bat {
        private ContinuousConfiguration configuration;
        private Integer[] position;
        private Integer[] velocity;
        private double frequency;
        private double initialRate;
        private double rate;
        private double loudness;
        private double score;

        public Bat() {
            configuration = new ContinuousConfiguration(currentDocument);
            position = new Integer[currentDocument.size()];
            velocity = new Integer[currentDocument.size()];
            for (int i = 0; i < currentDocument.size(); ++i) {
                position[i] = configuration.getAssignment(i);
                velocity[i] = 0;
            }
            frequency = randomDoubleInRange(minFrequency, maxFrequency);
            initialRate = randomDoubleInRange(minRate, maxRate);
            rate = initialRate;
            loudness = randomDoubleInRange(minLoudness, maxLoudness);
            recomputeScore();
        }

        public void recomputeScore() {
            configuration.setSenses(position);
            score = configurationScorer.computeScore(currentDocument, configuration);
        }

    }

}
