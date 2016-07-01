package org.getalp.lexsema.wsd.method.cuckoo;

import java.util.Random;
import org.apache.commons.math3.distribution.LevyDistribution;
import org.getalp.lexsema.wsd.method.StopCondition;

public class CuckooSearch 
{
    private class Cuckoo implements Runnable 
    {
        private final LevyDistribution levyDistribution;

        Cuckoo() 
        {
            double levyLocation = randomDoubleInRange(minLevyLocation, maxLevyLocation);
            double levyScale = randomDoubleInRange(minLevyScale, maxLevyScale);
            levyDistribution = new LevyDistribution(levyLocation, levyScale);
        }

        @Override
        public void run() 
        {
            while (!stopCondition.stop()) 
            {
                CuckooNest newNest = null;
                synchronized (nest) 
                {
                	newNest = nest.clone();
                }
                double distance = levyDistribution.sample();
                newNest.move(distance);
                double newScore = getScore(newNest);
                if (newScore > score) 
                {
                    synchronized (nest) 
                    {
                        nest = newNest;
                    }
                    score = newScore;
                }
                stopCondition.updateMilliseconds();
            }
        }
    }

    private final double minLevyLocation;

    private final double maxLevyLocation;

    private final double minLevyScale;

    private final double maxLevyScale;

    private static final Random random = new Random();

    private final StopCondition stopCondition;
    
    private final Thread[] cuckooThreads;

    private CuckooNest nest;

    private double score = 0.0;

    public CuckooSearch(int iterations, double levyLocation, double levyScale) 
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), levyLocation, levyLocation, levyScale, levyScale, Runtime.getRuntime().availableProcessors());
    }

    public CuckooSearch(int iterations, CuckooNest initialNest) 
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), 1, 10, 1, 10, Runtime.getRuntime().availableProcessors());
    }

    public CuckooSearch(int iterations, double minLevyLocation, double maxLevyLocation, double minLevyScale, double maxLevyScale) 
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, Runtime.getRuntime().availableProcessors());
    }

    public CuckooSearch(int iterations, double minLevyLocation, double maxLevyLocation, double minLevyScale, double maxLevyScale, int numberThreads) 
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), minLevyLocation, maxLevyLocation, minLevyScale, maxLevyScale, numberThreads);
    }

    public CuckooSearch(StopCondition stopCondition, double minLevyLocation, double maxLevyLocation, double minLevyScale, double maxLevyScale, int numberThreads) 
    {
        this.stopCondition = stopCondition;
        this.minLevyLocation = minLevyLocation;
        this.maxLevyLocation = maxLevyLocation;
        this.minLevyScale = minLevyScale;
        this.maxLevyScale = maxLevyScale;
        cuckooThreads = new Thread[numberThreads];
    }

    public CuckooNest start(CuckooNest initialNest) 
    {
        stopCondition.reset();
        nest = initialNest;
        score = getScore(nest);
        
        for (int i = 0; i < cuckooThreads.length; i++) 
        {
            cuckooThreads[i] = new Thread(new Cuckoo());
            cuckooThreads[i].start();
        }
        
        new Cuckoo().run();
        
        try 
        {
            for (Thread cuckoo : cuckooThreads) 
            {
                cuckoo.join();
            }
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }

        return nest;
    }

    private static double randomDoubleInRange(double min, double max) 
    {
        return (random.nextDouble() * (max - min)) + min;
    }

    private double getScore(CuckooNest nest) 
    {
        stopCondition.incrementScorerCalls();
        stopCondition.incrementIterations();
        return nest.score();
    }
}
