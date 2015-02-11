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

    public static final double T0_THRESHOLD = 0.01;
    private static Logger logger = LoggerFactory.getLogger(AdaptiveSimulatedAnnealing.class);
    private final double iterations = 100;
    boolean changedSinceLast = false;
    private int convergenceCycles;
    private Configuration configuration;
    private Configuration previousConfiguration;
    private double T;
    private double T0;
    private double p0;
    private int convergenceThreshold;
    private double m;
    private double n;
    private double latestDelta;
    private double bestScore;
    private double prevScore;
    private double currentCycle;
    private Random uniformGenerator = new Random();
    private ConfigurationScorer configurationScorer;


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
                logger.debug("[" + i + "][Avg Delta=" + avgDelta + "][T=" + formula + "][P0=" + Math.exp(-avgDelta / formula) + "]");
                currentThreshold += T0_THRESHOLD;
            }
            i++;
        } while (probability <= targetProbability);
        logger.info("[Avg Delta=" + avgDelta + "][T=" + formula + "][P0=" + Math.exp(-avgDelta / formula) + "]");
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

        List<Double> scores = new ArrayList<>();

        logger.info("[Evaluating initial parameters]");
        double sum;
        sum = 0;

        configuration = new ConfidenceConfiguration(document, ConfidenceConfiguration.InitializationType.RANDOM);

        //Execution of the algorithm nbEvaluation times
        for (int i = 0; i < iterations; i++) {
            //First set of scores for the initial execution is also the set of best scores
            double score =
                    configurationScorer.computeScore(document, makeRandomChange(configuration, document, uniformGenerator));
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
        logger.info(String.format(" [Avg=%s]", currScore));
    }

    //TODO: Factor in new configuration subtype;
    private Configuration makeRandomChange(Configuration source, Document document, Random gu) {
        Configuration newConfiguration = new ConfidenceConfiguration((ConfidenceConfiguration) source);
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
        logger.info(String.format("[Changing %s from %d to %d]", document.getWord(0, changeIndex).getId(), (int) value, (int) newValue));
        newConfiguration.setSense(changeIndex, (int) newValue);
        return newConfiguration;
    }

    @Override
    public Configuration disambiguate(Document document) {
        return disambiguate(document, new ConfidenceConfiguration(document));
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {

        initialize(document);

        while (evaluate()) {
            logger.info(String.format("[Cycle %s] [T=%s]", currentCycle, T));
            changedSinceLast = false;
            for (int j = 0; j < iterations; j++) {
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
        logger.info("[Adaptive random change in the parameters]");
        Configuration cp = makeRandomChange(configuration, document, uniformGenerator);

        score = configurationScorer.computeScore(document, makeRandomChange(configuration, document, uniformGenerator));

        //Checking if the change is accepted (non-significant treated as inferior)
        if (score > prevScore) {
            logger.info(String.format("[Change Better %s]", score));
            configuration = cp;
            prevScore = score;
            if (score > bestScore) {
                bestScore = score;
            }
            changedSinceLast = true;
        } else if (score <= prevScore) {
            latestDelta = Math.abs(prevScore - score);
            double delta = latestDelta;
            logger.info(String.format("[Lower Score %s][P(a)=%s][Ld=%s]", score, Math.exp(-latestDelta / T), latestDelta));

            //delta *= 100;
            Random r = new Random();
            double choice = r.nextDouble();
            double prob = Math.exp(-delta / T);
            if (prob > choice) {
                logger.info(String.format("Accepted lower score=%s", score));
                configuration = cp;
                prevScore = score;
                changedSinceLast = true;
            } else {
                logger.info("Keeping previous configuration");
            }
        }
    }

    private boolean evaluate() {
        T = calculateT(T0, currentCycle);

        if (convergenceCycles > convergenceThreshold
                && configuration.equals(previousConfiguration)) {
            return false;
        } else if (!changedSinceLast) {
            convergenceCycles++;
        } else {
            convergenceCycles = 0;
        }
        previousConfiguration = configuration;
        currentCycle++;
        logger.info(String.format("%s[Current Best Average Score=%s][%d]", System.lineSeparator(), bestScore, convergenceCycles));
        return true;
    }

    @Override
    public void release() {

    }
}
