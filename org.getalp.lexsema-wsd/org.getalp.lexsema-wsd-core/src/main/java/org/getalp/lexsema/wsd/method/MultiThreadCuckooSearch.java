package org.getalp.lexsema.wsd.method;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Random;

import org.apache.commons.math3.distribution.LevyDistribution;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThreadCuckooSearch {
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadCuckooSearch.class);

    public PrintWriter scorePlotWriter;

    public PrintWriter perfectScorePlotWriter;

    public ConfigurationScorer perfectScorer;

    private class Cuckoo implements Runnable {
        private final LevyDistribution levyDistribution;

        Cuckoo() {
            double levyLocation = randomDoubleInRange(minLevyLocation, maxLevyLocation);
            double levyScale = randomDoubleInRange(minLevyScale, maxLevyScale);
            levyDistribution = new LevyDistribution(levyLocation, levyScale);
        }

        @Override
        public void run() {
            while (!stopCondition.stop()) {
                ContinuousConfiguration newConfig = null;
                synchronized (configuration) {
                    newConfig = configuration.clone();
                }
                double distance = levyDistribution.sample();
                newConfig.makeRandomChanges((int) distance);
                double newScore = getScore(newConfig);
                if (newScore > score) {
                    synchronized (configuration) {
                        configuration = newConfig;
                    }
                    score = newScore;
                }
            }
        }
    }

    private final double minLevyLocation;

    private final double maxLevyLocation;

    private final double minLevyScale;

    private final double maxLevyScale;

    private static final Random random = new Random();

    private final StopCondition stopCondition;

    private final ConfigurationScorer configurationScorer;

    private final Thread[] cuckooThreads;

    private final boolean verbose;

    private ContinuousConfiguration configuration;

    private double score = 0.0;

    private Document currentDocument;

    public MultiThreadCuckooSearch(int iterations, double levyLocation, double levyScale, ConfigurationScorer configurationScorer, boolean verbose) {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), levyLocation, levyLocation, levyScale, levyScale, Runtime.getRuntime().availableProcessors(), configurationScorer, verbose);
    }

    public MultiThreadCuckooSearch(int iterations, ConfigurationScorer configurationScorer, boolean verbose) {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), 1, 10, 1, 10, Runtime.getRuntime().availableProcessors(), configurationScorer, verbose);
    }

    public MultiThreadCuckooSearch(int iterations, double minLevyLocation, double maxLevyLocation, double minLevyScale, double maxLevyScale, int numberThreads, ConfigurationScorer configurationScorer, boolean verbose) {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, numberThreads, configurationScorer, verbose);
    }

    public MultiThreadCuckooSearch(StopCondition stopCondition, double minLevyLocation, double maxLevyLocation, double minLevyScale, double maxLevyScale, int numberThreads, ConfigurationScorer configurationScorer, boolean verbose) {
        this.stopCondition = stopCondition;
        this.minLevyLocation = minLevyLocation;
        this.maxLevyLocation = maxLevyLocation;
        this.minLevyScale = minLevyScale;
        this.maxLevyScale = maxLevyScale;
        this.configurationScorer = configurationScorer;
        cuckooThreads = new Thread[numberThreads];
        this.verbose = verbose;
    }

    public Configuration disambiguate(Document document) throws InterruptedException {
        currentDocument = document;
        stopCondition.reset();
        configuration = new ContinuousConfiguration(currentDocument);
        score = getScore(configuration);
        for (int i = 0; i < cuckooThreads.length; i++) {
            cuckooThreads[i] = new Thread(new Cuckoo());
            cuckooThreads[i].start();
        }
        double prevProgressPercent = 0;
        while (!stopCondition.stop()) {
            int progress = (int) (stopCondition.getRemainingPercentage() * 100);
            double progressPercent = progress / 100.0;

            if (Math.abs(prevProgressPercent - progressPercent) > 1d) {
                logger.info(String.format("Cuckoo Progress : %2.2f%% - Current best : %.2f \r", progressPercent, score));
                prevProgressPercent = progressPercent;
            }
            stopCondition.updateMilliseconds();
            if (scorePlotWriter != null) {
                scorePlotWriter.println(MessageFormat.format("{0} {1}", stopCondition.getCurrent(), score));
            }
            if (perfectScorePlotWriter != null && perfectScorer != null) {
                perfectScorePlotWriter.println(MessageFormat.format("{0} {1}", stopCondition.getCurrent(), perfectScorer.computeScore(document, configuration)));
            }
        }
        for (Thread cuckoo : cuckooThreads) {
            cuckoo.join();
        }
        if (scorePlotWriter != null) {
            scorePlotWriter.flush();
        }
        if (perfectScorePlotWriter != null) {
            perfectScorePlotWriter.flush();
        }
        return configuration;
    }

    private static double randomDoubleInRange(double min, double max) {
        return (random.nextDouble() * (max - min)) + min;
    }

    private double getScore(Configuration config) {
        stopCondition.incrementScorerCalls();
        stopCondition.incrementIterations();
        return configurationScorer.computeScore(currentDocument, config);
    }

    public Configuration disambiguate(Document document, Configuration c) throws InterruptedException {
        return disambiguate(document);
    }

    public void release() {
        configurationScorer.release();
    }
}
