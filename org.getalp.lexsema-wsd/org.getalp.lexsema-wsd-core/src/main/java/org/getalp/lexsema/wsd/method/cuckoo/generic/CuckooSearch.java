package org.getalp.lexsema.wsd.method.cuckoo.generic;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.distribution.LevyDistribution;
import org.getalp.lexsema.wsd.method.IterationStopCondition;
import org.getalp.lexsema.wsd.method.StopCondition;

public class CuckooSearch
{
    private static final Random random = new Random();
    
    private StopCondition stopCondition;
        
    private LevyDistribution levyDistribution;
    
    private int nestsNumber;
    
    private int destroyedNestsNumber;

    private CuckooSolutionScorer solutionScorer;
    
    private CuckooSolutionFactory solutionFactory;

    private CuckooNest[] nests;
    
    private boolean verbose;

    public CuckooSearch(int iterations, double levyScale, int nestsNumber, int destroyedNests, CuckooSolutionScorer solutionScorer, CuckooSolutionFactory solutionFactory, boolean verbose)
    {
        this(new IterationStopCondition(iterations), levyScale, nestsNumber, destroyedNests, solutionScorer, solutionFactory, verbose);
    }

    public CuckooSearch(StopCondition stopCondition, double levyScale, int nestsNumber, int destroyedNests, CuckooSolutionScorer solutionScorer, CuckooSolutionFactory solutionFactory, boolean verbose)
    {
        this.stopCondition = stopCondition;
        this.levyDistribution = new LevyDistribution(1, levyScale);
        this.nestsNumber = nestsNumber;
        this.destroyedNestsNumber = destroyedNests;
        this.solutionScorer = solutionScorer;
        this.solutionFactory = solutionFactory;
        nests = new CuckooNest[nestsNumber];
        this.verbose = verbose;
    }

    public CuckooSolution run()
    {
        if (nestsNumber == 1) return runWithSingleNest();
        else return runWithManyNests();
    }
    
    private CuckooSolution runWithSingleNest()
    {
        stopCondition.reset();
        nests[0] = new CuckooNest(solutionScorer, levyDistribution, solutionFactory);
        
        while (!stopCondition.stop())
        {
            int progress = (int)(stopCondition.getRemainingPercentage() * 100);
            double progressPercent = (double)progress / 100.0;
            CuckooNest newNest = nests[0].clone();
            newNest.randomFly();
            if (newNest.score > nests[0].score)
            {
                nests[0] = newNest;
            }
            if (verbose)
            {
                System.out.println("Cuckoo Progress : " + progressPercent + "% - " +
                                   "Current best : " + nests[0].score + 
                                   " [" + nests[0].solution + "]");
            }
            stopCondition.increment();
        }
        return nests[0].solution;
    }
    
    private CuckooSolution runWithManyNests()
    {
        stopCondition.reset();
        
        for (int i = 0 ; i < nestsNumber ; i++)
        {
            nests[i] = new CuckooNest(solutionScorer, levyDistribution, solutionFactory);
        }
        
        while (!stopCondition.stop())
        {
            int progress = (int)(stopCondition.getRemainingPercentage() * 100);
            double progressPercent = (double)progress / 100.0;
            
            int i = random.nextInt(nests.length);
            CuckooNest new_i = nests[i].clone();
            new_i.randomFly();
            
            int j = random.nextInt(nests.length);
            while (j == i) j = random.nextInt(nests.length);
            
            if (new_i.score > nests[j].score)
            {
                nests[j] = new_i;
            }
            
            sortNests();
            abandonWorthlessNests();

            if (verbose)
            {
                System.out.println("Cuckoo Progress : " + progressPercent + "% - " +
                                   "Current best : " + nests[nestsNumber - 1].score + 
                                   " [" + nests[nestsNumber - 1].solution + "]");
            }
            stopCondition.increment();
        }
        sortNests();
        return nests[nestsNumber - 1].solution;
    }

    private void sortNests()
    {
        Arrays.sort(nests);
    }

    private void abandonWorthlessNests()
    {
        for (int i = 0 ; i < destroyedNestsNumber ; i++)
        {
            nests[i] = new CuckooNest(solutionScorer, levyDistribution, solutionFactory);
        }
    }
}
