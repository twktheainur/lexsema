package org.getalp.lexsema.wsd.method;

import java.io.PrintWriter;
import java.util.Random;

import org.apache.commons.math3.distribution.LevyDistribution;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class MultiThreadCuckooSearch
{
    public PrintWriter scorePlotWriter = null;

    public PrintWriter perfectScorePlotWriter = null;
    
    public ConfigurationScorer perfectScorer = null;
    
    private class Cuckoo extends Thread implements Runnable 
    {
        private LevyDistribution levyDistribution;
        
        public Cuckoo()
        {
            double levyLocation = randomDoubleInRange(minLevyLocation, maxLevyLocation);
            double levyScale = randomDoubleInRange(minLevyScale, maxLevyScale);
            this.levyDistribution = new LevyDistribution(levyLocation, levyScale);
        }

        public void run()
        {
            while (!stopCondition.stop())
            {
                ContinuousConfiguration newConfig = null;
                synchronized (configuration)
                {
                    newConfig = configuration.clone();
                }
                double distance = levyDistribution.sample();
                newConfig.makeRandomChanges((int) distance);
                double newScore = getScore(newConfig);
                if (newScore > score)
                {
                    synchronized (configuration)
                    {
                        configuration = newConfig;
                    }
                    score = newScore;
                }
            }
        }
    }

    private double minLevyLocation;
    
    private double maxLevyLocation;
    
    private double minLevyScale;
    
    private double maxLevyScale;
    
    private static final Random random = new Random();
    
    private StopCondition stopCondition;

    private ConfigurationScorer configurationScorer;

    private Cuckoo[] cuckoos;;
    
    private boolean verbose;

    public ContinuousConfiguration configuration;
    
    private double score;
    
    private Document currentDocument;

    public MultiThreadCuckooSearch(int iterations, double levyLocation, double levyScale, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), levyLocation, levyLocation, levyScale, levyScale, Runtime.getRuntime().availableProcessors(), configurationScorer, verbose);
    }

    public MultiThreadCuckooSearch(int iterations, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), 1, 10, 1, 10, Runtime.getRuntime().availableProcessors(), configurationScorer, verbose);
    }

    public MultiThreadCuckooSearch(int iterations, double minLevyLocation, double maxLevyLocation, double minLevyScale, double maxLevyScale, int numberThreads, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, numberThreads, configurationScorer, verbose);
    }

    public MultiThreadCuckooSearch(StopCondition stopCondition, double minLevyLocation, double maxLevyLocation, double minLevyScale, double maxLevyScale, int numberThreads, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this.stopCondition = stopCondition;
        this.minLevyLocation = minLevyLocation;
        this.maxLevyLocation = maxLevyLocation;
        this.minLevyScale = minLevyScale;
        this.maxLevyScale = maxLevyScale;
        this.configurationScorer = configurationScorer;
        this.cuckoos = new Cuckoo[numberThreads];
        this.verbose = verbose;
    }

    public Configuration disambiguate(Document document) throws Exception
    {
        currentDocument = document;
        stopCondition.reset();
        configuration = new ContinuousConfiguration(currentDocument);
        score = getScore(configuration);
        for (int i = 0 ; i < cuckoos.length ; i++)
        {
            cuckoos[i] = new Cuckoo();
            cuckoos[i].start();
        }
        while (!stopCondition.stop())
        {
            int progress = (int)(stopCondition.getRemainingPercentage() * 100);
            double progressPercent = (double)progress / 100.0;
            if (verbose)
            {
                System.out.println("Cuckoo Progress : " + progressPercent + "% - " +
                                   "Current best : " + score);
            }
            stopCondition.updateMilliseconds();
            if (scorePlotWriter != null) scorePlotWriter.println(stopCondition.getCurrent() + " " + score);
            if (perfectScorePlotWriter != null && perfectScorer != null) perfectScorePlotWriter.println(stopCondition.getCurrent() + " " + perfectScorer.computeScore(document, configuration));
        }
        for (int i = 0 ; i < cuckoos.length ; i++)
        {
            cuckoos[i].join();
        }
        if (scorePlotWriter != null) scorePlotWriter.flush();
        if (perfectScorePlotWriter != null) perfectScorePlotWriter.flush();
        return this.configuration;
    }
   
    private static double randomDoubleInRange(double min, double max) 
    {
        return (random.nextDouble() * (max - min)) + min;
    }
    
    private double getScore(Configuration config)
    {
        stopCondition.incrementScorerCalls();
        stopCondition.incrementIterations();
        return configurationScorer.computeScore(currentDocument, config);
    }

    public Configuration disambiguate(Document document, Configuration c) throws Exception
    {
        return disambiguate(document);
    }
    
    public void release()
    {
        configurationScorer.release();
    }
}
