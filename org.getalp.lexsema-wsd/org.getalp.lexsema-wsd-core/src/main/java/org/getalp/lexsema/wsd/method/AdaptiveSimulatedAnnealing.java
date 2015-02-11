package org.getalp.lexsema.wsd.method;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdaptiveSimulatedAnnealing implements Disambiguator {
    /**
     * Constant parameters
     */
    public static final double T0_THRESHOLD = 0.01;
    public static final double ITERATIONS = 100;
    public static final int NUMBER_OF_CHANGES = 20;
    private static Logger logger = LoggerFactory.getLogger(AdaptiveSimulatedAnnealing.class);
    boolean changedSinceLast = false;
    private Random uniformGenerator = new Random();
    /**
     * Estimated parameters
     */
    private double T;
    private double T0;
    /**
     * User-supplied parameters
     */
    private double p0;
    private int convergenceThreshold;
    private double m;
    private double n;
    /**
     * Configuration state
     */
    private Configuration configuration;
    private Configuration previousConfiguration;
    private ConfigurationScorer configurationScorer;
    /**
     * Current algorithm state
     */
    private double latestDelta;
    private double bestScore;
    private double prevScore;
    private int currentCycle;
    private int convergenceCycles;

    public AdaptiveSimulatedAnnealing(double p0, double m, double n, int convergenceThreshold, int numberThreads, SimilarityMeasure similarityMeasure) {
        this.convergenceThreshold = convergenceThreshold;
        this.m = m;
        this.n = n;
        this.p0 = p0;
        configurationScorer = new TverskyConfigurationScorer(similarityMeasure, numberThreads);
    }

    /**
     * Iteratively determins the initial Temperature, T0 that corresponds to the targetProbability for a given average
     * difference between successive scores
     *
     * @param avgDelta          Average score difference between executions
     * @param targetProbability Initial acceptance probability
     * @return Initial temperature
     */
    private double findT0(double avgDelta, double targetProbability) {
        logger.info("Searching for T0...");
        int i = 2;
        double formula;
        double probability;
        double currentThreshold = T0_THRESHOLD;
        do {
            formula = avgDelta / Math.exp(1 - 1.0 / (i + (double) 2)) * Math.log(i + 2);
            probability = Math.exp(-avgDelta / formula);
            if (probability > currentThreshold) {
                //logger.debug("[" + i + "][Avg Delta=" + avgDelta + "][T=" + formula + "][P0=" + Math.exp(-avgDelta / formula) + "]");
                currentThreshold += T0_THRESHOLD;
            }
            i++;
        } while (probability <= targetProbability);
        logger.info(String.format("[Avg Delta=%.4f][T=%.4f][P0=%.2f]", avgDelta, formula, Math.exp(-avgDelta / formula)));
        return formula;
    }

    private void initialize(Document document) {
        initialEvaluation(document);
        T0 = findT0(latestDelta, p0);
        T = T0;
        currentCycle = 0;
        convergenceCycles = 0;
        Configuration bestConfiguration = configuration;
    }

    /**
     * Calculate the temperature at a given cycle given the initial temperature, T0
     *
     * @param T0    Initial temperature
     * @param cycle Cycle number
     * @return the resulting temperature
     */
    private double calculateT(double T0, double cycle) {
        double c = m * Math.exp(-n / configuration.size());
        return T0
                * Math.exp(-c
                * Math.pow(cycle, 1.0 / (double) configuration.size()));
    }

    private void initialEvaluation(Document document) {
        logger.info("Sampling...");
        List<Double> scores = new ArrayList<>();
        double sum;
        sum = 0;

        configuration = new ConfidenceConfiguration(document, ConfidenceConfiguration.InitializationType.RANDOM);

        //Execution of the algorithm nbEvaluation times
        for (int i = 0; i < ITERATIONS; i++) {
            //First set of scores for the initial execution is also the set of best scores
            double score =
                    configurationScorer.computeScore(document, makeInitialRandomChange(configuration, document, NUMBER_OF_CHANGES, uniformGenerator));
            scores.add(score);
        }

        //Calculation of the average and of the average delta between successive scores
        prevScore = scores.get(0);
        double sumDelta = 0;
        for (double score : scores) {
            sum += score;
            sumDelta += Math.abs(score - prevScore);
        }
        double currScore = sum / scores.size();

        sumDelta /= scores.size() - 1;
        bestScore = currScore;
        prevScore = currScore;
        latestDelta = sumDelta;
    }

    //TODO: Factor in new configuration subtype;
    private Configuration makeRandomChange(Configuration source, Document document, int numberOfChanges, Random gu) {
        Configuration newConfiguration = new ConfidenceConfiguration((ConfidenceConfiguration) source);

        for (int i = 0; i < numberOfChanges; i++) {
            int changeIndex = gu.nextInt(newConfiguration.size());
            final double centeringConstant = 0.5d;

            int min = 0;
            int max = document.getSenses(changeIndex).size();
            double value = newConfiguration.getAssignment(changeIndex);

            double u = gu.nextDouble();
            double y = Math.signum(u - centeringConstant) * T
                    * (Math.pow(1 + 1 / T, Math.abs(2 * u - 1)) - 1);
            double newValue = value + y * (max - min);
            while (newValue < min || newValue > max) {
                u = gu.nextDouble();
                y = Math.signum(u - centeringConstant) * T
                        * (Math.pow(1 + 1 / T, Math.abs(2 * u - 1)) - 1);
                newValue = value + y * (max - min);
            }
            //logger.trace(String.format("\t[Changing %s from %d to %d]", document.getWord(0, changeIndex).getId(), (int) value, (int) newValue));
            newConfiguration.setSense(changeIndex, (int) newValue);
        }
        return newConfiguration;
    }

    private Configuration makeInitialRandomChange(Configuration source, Document document, int numberOfChanges, Random gu) {
        Configuration newConfiguration = new ConfidenceConfiguration((ConfidenceConfiguration) source);

        for (int i = 0; i < numberOfChanges; i++) {
            int changeIndex = gu.nextInt(newConfiguration.size());
            int max = document.getSenses(changeIndex).size();
            newConfiguration.setSense(changeIndex, gu.nextInt(max));
        }
        return newConfiguration;
    }

    private Configuration makeRandomChange(Configuration source, Document document, Random gu) {
        return makeRandomChange(source, document, 1, gu);
    }

    @Override
    public Configuration disambiguate(Document document) {
        return disambiguate(document, new ConfidenceConfiguration(document));
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {

        initialize(document);

        while (evaluate()) {
            logger.info(String.format("[Cycle %s] [T=%s] [Convergence: %d/%d]", currentCycle, T, convergenceCycles, convergenceThreshold));
            changedSinceLast = false;
            for (int j = 0; j < ITERATIONS; j++) {
                anneal(document);
            }
        }

        return configuration;
    }

    protected void anneal(Document document) {
        double score;
        score = 0;
        //Making random changes -- Uniformly selects a number of parameters to change, and changes their value randomly
        //Following the formula from Adaptive SA (http://www.ingber.com/#ASA)
        Configuration cp = makeRandomChange(configuration, document, uniformGenerator);

        score = configurationScorer.computeScore(document, makeRandomChange(configuration, document, NUMBER_OF_CHANGES, uniformGenerator));

        //Checking if the change is accepted (non-significant treated as inferior)
        if (score > prevScore) {
            logger.info(String.format("\t[Cycle=%d][Better Score = %.2f][Best = %.2f][P(a)=%.2f][Ld=%.2f]", currentCycle, score, bestScore, Math.exp(-latestDelta / T), latestDelta));
            configuration = cp;
            prevScore = score;
            if (score > bestScore) {
                bestScore = score;
            }
            changedSinceLast = true;
        } else if (score <= prevScore) {
            latestDelta = Math.abs(prevScore - score);
            double delta = latestDelta;
            Random r = new Random();
            double choice = r.nextDouble();
            double prob = Math.exp(-delta / T);
            if (prob > choice) {
                logger.info(String.format("\t[Cycle=%d][Accepted Lower Score = %.2f][Best = %.2f][P(a)=%.2f][Ld=%.2f]", currentCycle, score, bestScore, Math.exp(-latestDelta / T), latestDelta));
                configuration = cp;
                prevScore = score;
                changedSinceLast = true;
            }
        }
    }

    private boolean evaluate() {
        T = calculateT(T0, currentCycle);

        if (convergenceCycles >= convergenceThreshold
                && configuration.equals(previousConfiguration)) {
            return false;
        } else if (!changedSinceLast) {
            convergenceCycles++;
        } else {
            convergenceCycles = 0;
        }
        previousConfiguration = configuration;
        currentCycle++;
        return true;
    }

    @Override
    public void release() {
        configurationScorer.release();
    }
}
