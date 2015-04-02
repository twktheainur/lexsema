package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
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

    private Document currentDocument;

    private ConfigurationScorer configurationScorer;

    private Bat[] bats;

    private Bat bestBat;

    private class Bat
    {
        private ContinuousConfiguration configuration;
        private int[] position;
        private int[] velocity;
        private double frequency;
        private double initialRate;
        private double rate;
        private double loudness;
        private int[] previousPosition;
        private int[] previousVelocity;
        private boolean needRecomputeScore;
        private double score;

        public Bat()
        {
            configuration = new ContinuousConfiguration(currentDocument);
            position = new int[currentDocument.size()];
            velocity = new int[currentDocument.size()];
            for (int i = 0; i < currentDocument.size(); ++i)
            {
                position[i] = configuration.getAssignment(i);
                velocity[i] = 0;
            }
            frequency = randomDoubleInRange(minFrequency, maxFrequency);
            initialRate = randomDoubleInRange(minRate, maxRate);
            rate = initialRate;
            loudness = randomDoubleInRange(minLoudness, maxLoudness);
            previousPosition = position;
            previousVelocity = velocity;
            needRecomputeScore = true;
            score = getScore();
        }

        public void setPosition(int[] newPosition)
        {
            position = newPosition;
            needRecomputeScore = true;
        }

        public double getScore()
        {
            if (needRecomputeScore)
            {
                configuration.setSenses(position);
                score = configurationScorer.computeScore(currentDocument, configuration);
                needRecomputeScore = false;
            }
            return score;
        }

        public void savePositionAndVelocity()
        {
            previousPosition = position.clone();
            previousVelocity = velocity.clone();
        }

        public void restorePositionAndVelocity()
        {
            position = previousPosition;
            velocity = previousVelocity;
            needRecomputeScore = true;
        }
    }
    
    public BatAlgorithm(int iterationsNumber, int batsNumber, double minFrequency, double maxFrequency, 
                        double minLoudness, double maxLoudness, double minRate, double MaxRate, 
                        double alpha, double gamma, SimilarityMeasure similarityMeasure)
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
        int threadsNumber = Runtime.getRuntime().availableProcessors();
        configurationScorer = new TverskyConfigurationScorer(similarityMeasure, threadsNumber);
        bats = new Bat[batsNumber];
    }

    public Configuration disambiguate(Document document)
    {
        currentDocument = document;
        
        for (int i = 0 ; i < batsNumber ; ++i)
        {
            bats[i] = new Bat();
        }

        updateBestBat();

        for (int currentIteration = 0 ; currentIteration < iterationsNumber ; currentIteration++)
        {
            int progress = (int)(((double) currentIteration / (double) iterationsNumber) * 10000);
            System.out.println("Bat progress : " + (double)progress / 100.0 + "%");

            for (Bat currentBat : bats)
            {
                currentBat.frequency = randomDoubleInRange(minFrequency, maxFrequency);

                currentBat.savePositionAndVelocity();

                currentBat.velocity = add(
                        currentBat.velocity,
                        multiply(substract(currentBat.position, bestBat.position),
                                currentBat.frequency));
                currentBat.setPosition(add(currentBat.position, currentBat.velocity));

                if (currentBat.rate < randomDoubleInRange(minRate, maxRate))
                {
                    // fly randomly around best solutions ?
                }

                currentBat.setPosition(add(currentBat.position,
                                           randomDoubleInRange(-1, 1) * computeAverageLoudness()));

                if (currentBat.loudness > randomDoubleInRange(minLoudness, maxLoudness) &&
                    currentBat.getScore() > bestBat.getScore())
                {
                    currentBat.loudness *= alpha;
                    currentBat.rate = currentBat.initialRate * (1 - Math.exp(-gamma * currentIteration));
                }
                else
                {
                    currentBat.restorePositionAndVelocity();
                }
            }

            updateBestBat();
            
            System.out.println("Current best : " + bestBat.getScore());
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

    private int[] substract(int[] leftOperand, int[] rightOperand)
    {
        int[] result = new int[Math.min(leftOperand.length, rightOperand.length)];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = leftOperand[i] - rightOperand[i];
        }
        return result;
    }

    private int[] multiply(int[] leftOperand, double rightOperand)
    {
        int[] result = new int[leftOperand.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = (int) (leftOperand[i] * rightOperand);
        }
        return result;
    }

    private int[] add(int[] leftOperand, int[] rightOperand)
    {
        int[] result = new int[Math.min(leftOperand.length, rightOperand.length)];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = leftOperand[i] + rightOperand[i];
        }
        return result;
    }

    private int[] add(int[] leftOperand, double rightOperand)
    {
        int[] result = new int[leftOperand.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = (int) (leftOperand[i] + rightOperand);
        }
        return result;
    }

    private void updateBestBat()
    {
        double bestScore = Double.MIN_VALUE;
        for (Bat currentBat : bats)
        {
            double currentScore = currentBat.getScore();
            if (currentScore > bestScore)
            {
                bestScore = currentScore;
                bestBat = currentBat;
            }
        }
    }

    private double computeAverageLoudness()
    {
        double loudnessesSum = 0;
        for (Bat currentBat : bats)
        {
            loudnessesSum += currentBat.loudness;
        }
        return loudnessesSum / (double) batsNumber;
    }

}
