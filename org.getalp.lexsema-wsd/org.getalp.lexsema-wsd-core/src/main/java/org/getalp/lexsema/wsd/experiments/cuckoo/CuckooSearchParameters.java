package org.getalp.lexsema.wsd.experiments.cuckoo;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.distribution.LevyDistribution;

public class CuckooSearchParameters
{
    private static final Random random = new Random();
    
    private int iterationsNumber;

    private LevyDistribution levyDistribution;
    
    private int nestsNumber;
    
    private int destroyedNestsNumber;

    private CuckooParametersScorer parametersScorer;

    private Nest[] nests;
    
    private class Nest implements Comparable<Nest>
    {
       public CuckooParameters params;
       public double score;
       public Nest()
       {
           params = new CuckooParameters();
           score = parametersScorer.computeScore(params);
       }
       public Nest(CuckooParameters params, double score)
       {
           this.params = params.clone();
           this.score = score;
       }
       public int compareTo(Nest other)
       {
            return Double.compare(score, other.score);
       }
       public Nest clone()
       {
           return new Nest(params.clone(), score);
       }
       public void randomFly()
       {
           double distance = levyDistribution.sample();
           CuckooParameters parametersBackup = params.clone();
           double scoreBackup = score;
           params.makeRandomChanges(distance);
           score = parametersScorer.computeScore(params);
           if (score < scoreBackup)
           {
               params = parametersBackup;
               score = scoreBackup;
           }
       }
    }

    public CuckooSearchParameters(int iterations, double levyScale, int nestsNumber, int destroyedNests,
            CuckooParametersScorer parametersScorer)
    {
        this.iterationsNumber = iterations;
        this.levyDistribution = new LevyDistribution(1, levyScale);
        this.nestsNumber = nestsNumber;
        this.destroyedNestsNumber = destroyedNests;
        this.parametersScorer = parametersScorer;
        nests = new Nest[nestsNumber];
    }

    public CuckooParameters run()
    {
        for (int i = 0 ; i < nestsNumber ; i++)
        {
            nests[i] = new Nest();
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
                               "Current best : " + nests[nestsNumber - 1].score + " [" + nests[nestsNumber - 1].params + "]");
        }
        sortNests();
        return nests[nestsNumber - 1].params;
    }

    private void sortNests()
    {
        Arrays.sort(nests);
    }

    private void abandonWorthlessNests()
    {
        for (int i = 0 ; i < destroyedNestsNumber ; i++)
        {
            nests[i] = new Nest();
        }
    }
}