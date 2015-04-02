package org.getalp.lexsema.wsd.method;

import cern.colt.matrix.Norm;
import cern.jet.random.tdouble.engine.DoubleMersenneTwister;
import cern.jet.random.tdouble.engine.DoubleRandomEngine;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.util.ValueScale;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.MatrixTverskiConfigurationScorer;
import org.getalp.ml.matrix.score.NormMatrixScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class SimulatedAnnealing implements Disambiguator {
    /**
     * Constant parameters
     */
    public static final double T0_THRESHOLD = 0.01;
    public static final int NUMBER_OF_CHANGES = 10;
    private static Logger logger = LoggerFactory.getLogger(SimulatedAnnealing.class);
    public double iterations = 1000;
    boolean changedSinceLast = false;
    private DoubleRandomEngine uniformGenerator = new DoubleMersenneTwister();
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
    private double coolingRate;
    /**
     * Configuration state
     */
    private Configuration configuration;
    private Configuration bestConfiguration;
    private Configuration previousConfiguration;
    private ConfigurationScorer configurationScorer;
    /**
     * Current algorithm state
     */
    private double delta;
    private double maxDelta;
    private double bestScore;
    private double prevScore;
    private double currentCycle;
    private int convergenceCycles;
    private int numberOfAcceptanceEvents = 0;

    public SimulatedAnnealing(double p0, double coolingRate, int convergenceThreshold, int iterations, int numberThreads, SimilarityMeasure similarityMeasure) {
        this.convergenceThreshold = convergenceThreshold;
        this.p0 = p0;
        configurationScorer = new MatrixTverskiConfigurationScorer(similarityMeasure, null, new NormMatrixScorer(Norm.Frobenius), numberThreads);
        this.coolingRate = coolingRate;
        this.iterations = iterations;

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
        maxDelta = delta;
        T0 = findT0(delta, p0);
        T = T0;
        currentCycle = 0;
        convergenceCycles = 0;
        bestConfiguration = configuration;
    }

    /**
     * Calculate the temperature at a given cycle given the initial temperature, T0
     *
     * @param T0    Initial temperature
     * @param cycle Cycle number
     * @return the resulting temperature
     */
    private double calculateT(double T0, double cycle) {
        return T0
                * Math.pow(coolingRate, cycle);
    }

    private void initialEvaluation(Document document) {
        logger.info("Sampling...");
        List<Double> scores = new ArrayList<>();
        double sum;
        sum = 0;

        configuration = new ConfidenceConfiguration(document, ConfidenceConfiguration.InitializationType.RANDOM);

        //Execution of the algorithm nbEvaluation times
        for (int i = 0; i < iterations; i++) {
            //First set of scores for the initial execution is also the set of best scores
            double score =
                    configurationScorer.computeScore(document, makeRandomChange(configuration, document, NUMBER_OF_CHANGES, uniformGenerator));
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
        delta = sumDelta;
    }

    private int nextRandomNatural(DoubleRandomEngine randomEngine, int max) {
        return (int) ValueScale.scaleValue(randomEngine.raw(), 0d, 1d, 0, max);
    }

    //TODO: Factor in new configuration subtype;
    private Configuration makeRandomChange(Configuration source, Document document, int numberOfChanges, DoubleRandomEngine gu) {
        Configuration newConfiguration = new ConfidenceConfiguration((ConfidenceConfiguration) source);

        for (int i = 0; i < numberOfChanges; i++) {
            int changeIndex = nextRandomNatural(gu, source.size());
            int numberOfSenses = document.getSenses(changeIndex).size();
            int newIndex = nextRandomNatural(gu, numberOfSenses);
            newConfiguration.setSense(changeIndex, newIndex);
        }
        return newConfiguration;
    }

    private Configuration makeRandomChange(Configuration source, Document document, DoubleRandomEngine gu) {
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
            logger.info(String.format("[Cycle %.2f] [T=%.2f] [Convergence: %d/%d]", currentCycle, T, convergenceCycles, convergenceThreshold));
            changedSinceLast = false;
            for (int j = 0; j < iterations; j++) {
                anneal(document, j);
            }
        }

        return configuration;
    }

    protected void anneal(Document document, int cycleNumber) {
        double score;
        score = 0;
        //Making random changes -- Uniformly selects a number of parameters to change, and changes their value randomly
        //Following the formula from Adaptive SA (http://www.ingber.com/#ASA)
        Configuration cp = makeRandomChange(configuration, document, uniformGenerator);

        score = configurationScorer.computeScore(document, makeRandomChange(configuration, document, NUMBER_OF_CHANGES, uniformGenerator));

        //Checking if the change is accepted (non-significant treated as inferior)
        if (score > prevScore) {
            //logger.info(String.format("\r\t[Cycle=%f | %.2f%%][Better Score = %.2f][Best = %.2f][P(a)=%.2f][Ld=%.2f]", currentCycle, (double) cycleNumber / iterations * 100d, score, bestScore, Math.exp(-delta / T), delta));
            configuration = cp;
            prevScore = score;
            if (score > bestScore) {
                bestScore = score;
                bestConfiguration = configuration;
            }
            changedSinceLast = true;
            numberOfAcceptanceEvents++;
        } else if (score <= prevScore) {
            delta = Math.abs(prevScore - score);
            if (delta > maxDelta) {
                maxDelta = delta;
            }
            double choice = uniformGenerator.raw();
            double prob = Math.exp(-delta / T);
            if (prob > choice) {
                //logger.info(String.format("\r\t[Cycle=%f | %.2f%%][Accepted Lower Score = %.2f][Best = %.2f][P(a)=%.2f][Ld=%.2f]", currentCycle, (double) cycleNumber / iterations * 100d, score, bestScore, Math.exp(-delta / T), delta));
                configuration = cp;
                prevScore = score;
                changedSinceLast = true;
                numberOfAcceptanceEvents++;
            }
        }
        if (numberOfAcceptanceEvents > 100) {
            numberOfAcceptanceEvents = 0;
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
