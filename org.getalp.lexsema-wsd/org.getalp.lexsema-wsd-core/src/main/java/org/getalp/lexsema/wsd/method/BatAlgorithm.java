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
    private static final int batsNumber = 20;

    private static final int iterationsNumber = 1000;

    private static final double minFrequency = 0;

    private static final double maxFrequency = 20;

    private static final double minLoudness = 0;

    private static final double maxLoudness = 10;

    private static final double minRate = 0;

    private static final double maxRate = 1;

    private static final double alpha = 0.95;

    private static final double gamma = 0.9;

    private static final Random random = new Random();

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
    
    private Document currentDocument;

    private ConfigurationScorer configurationScorer;

    private Bat[] bats = new Bat[batsNumber];

    private Bat bestBat;

    public BatAlgorithm(SimilarityMeasure similarityMeasure)
    {
        int threadsNumber = Runtime.getRuntime().availableProcessors();
        configurationScorer = new TverskyConfigurationScorer(similarityMeasure, threadsNumber);
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

            for (int i = 0; i < batsNumber; i++)
            {
                Bat currentBat = bats[i];

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
