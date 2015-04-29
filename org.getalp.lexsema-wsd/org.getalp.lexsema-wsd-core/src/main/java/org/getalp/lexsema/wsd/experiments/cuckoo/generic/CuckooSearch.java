package org.getalp.lexsema.wsd.experiments.cuckoo.generic;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.distribution.LevyDistribution;

public class CuckooSearch
{
    private static final Random random = new Random();
    
    private int iterationsNumber;

    private LevyDistribution levyDistribution;
    
    private int nestsNumber;
    
    private int destroyedNestsNumber;

    private CuckooSolutionScorer solutionScorer;
    
    private CuckooSolutionFactory solutionFactory;

    private CuckooNest[] nests;

    public CuckooSearch(int iterations, double levyScale, int nestsNumber, int destroyedNests, CuckooSolutionScorer solutionScorer, CuckooSolutionFactory solutionFactory)
    {
        this.iterationsNumber = iterations;
        this.levyDistribution = new LevyDistribution(1, levyScale);
        this.nestsNumber = nestsNumber;
        this.destroyedNestsNumber = destroyedNests;
        this.solutionScorer = solutionScorer;
        this.solutionFactory = solutionFactory;
        nests = new CuckooNest[nestsNumber];
    }

    public CuckooSolution run()
    {
        for (int i = 0 ; i < nestsNumber ; i++)
        {
            nests[i] = new CuckooNest(solutionScorer, levyDistribution, solutionFactory);
        }
        
        for (int currentIteration = 0 ; currentIteration < iterationsNumber ; currentIteration++)
        {
            int progress = (int)(((double) currentIteration / (double) iterationsNumber) * 10000);
            double progressPercent = (double)progress / 100.0;
            
            int i = random.nextInt(nests.length);
            nests[i].randomFly();
            
            int j = random.nextInt(nests.length);
            
            if (nests[i].score > nests[j].score)
            {
                nests[j] = nests[i].clone();
            }
            
            sortNests();
            abandonWorthlessNests();

            System.out.println("Cuckoo Progress : " + progressPercent + "% - " +
                               "Current best : " + nests[nestsNumber - 1].score);
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
