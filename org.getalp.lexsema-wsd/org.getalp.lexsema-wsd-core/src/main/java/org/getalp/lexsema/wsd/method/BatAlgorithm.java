package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.BatConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BatAlgorithm implements Disambiguator
{
    private static final int batsNumber = 10;

    private static final int iterationsNumber = 10;

    private static final double minFrequency = 0;

    private static final double maxFrequency = 20;

    private static final double minLoudness = 0;

    private static final double maxLoudness = 100;

    private static final double minRate = 0;

    private static final double maxRate = 1;

    private static final double alpha = 0.9;

    private static final double gamma = 0.9;

    private static final Random random = new Random();

    private Document currentDocument;

    private ConfigurationScorer configurationScorer;

    private List<Bat> bats = new ArrayList<Bat>();

    private Bat bestBat;

    private class Bat
    {
        private BatConfiguration configuration;
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

        public void initialize(Document document)
        {
            configuration = new BatConfiguration(document);
            position = new int[document.size()];
            velocity = new int[document.size()];
            for (int i = 0; i < document.size(); ++i)
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

    public BatAlgorithm(SimilarityMeasure similarityMeasure)
    {
        int threadsNumber = Runtime.getRuntime().availableProcessors();
        configurationScorer = new TverskyConfigurationScorer(similarityMeasure, threadsNumber);
        for (int i = 0 ; i < batsNumber ; ++i)
        {
            bats.add(new Bat());
        }
    }

    @Override
    public Configuration disambiguate(Document document)
    {
        currentDocument = document;

        System.out.println("BatInitialization...");
        for (int i = 0; i < batsNumber; i++)
        {
            bats.get(i).initialize(document);
        }
        bestBat = getBestBat();

        for (int currentIteration = 0 ; currentIteration < iterationsNumber ; currentIteration++)
        {
            int progress = (int) (((double) currentIteration / (double) iterationsNumber) * 100);
            System.out.println("BatProgress : " + progress + "%");

            for (int i = 0; i < batsNumber; i++)
            {
                Bat currentBat = bats.get(i);

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
            
            bestBat = getBestBat();
        }

        return bestBat.configuration;
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c)
    {
        return disambiguate(document);
    }

    @Override
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

    private Bat getBestBat()
    {
        Bat bestBat = this.bestBat;
        for (int i = 0 ; i < batsNumber ; ++i)
        {
            bestBat = getBestBatBetween(bestBat, bats.get(i));
        }
        return bestBat;
    }

    private double computeAverageLoudness()
    {
        double loudnessesSum = 0;
        for (int i = 0; i < batsNumber; ++i)
        {
            loudnessesSum += bats.get(i).loudness;
        }
        return loudnessesSum / (double) batsNumber;
    }

    private Bat getBestBatBetween(Bat bat1, Bat bat2)
    {
        if (bat1 == null) return bat2;
        if (bat2 == null) return bat1;
        return bat1.getScore() > bat2.getScore() ? bat1 : bat2;
    }
}
