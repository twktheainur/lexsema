package org.getalp.lexsema.wsd.method;

import java.util.Arrays;
import java.util.Random;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
import org.apache.commons.math3.distribution.LevyDistribution;

public class CuckooSearch implements Disambiguator
{
    private static final Random random = new Random();
    
    private static final LevyDistribution levyDistribution = new LevyDistribution(1, 0.5);
    
    private static final int nestsNumber = 20;
    
    private static final int destroyedNestsNumber = 5;

    private static final int iterationsNumber = 5000;

    private class Nest implements Comparable<Nest>
    {
       public ContinuousConfiguration configuration;
       public double score;
       public Nest()
       {
           configuration = new ContinuousConfiguration(currentDocument);
           score = configurationScorer.computeScore(currentDocument, configuration);
       }
       public Nest(int[] position)
       {
           configuration = new ContinuousConfiguration(currentDocument, position);
           score = configurationScorer.computeScore(currentDocument, configuration);
       }
       public int compareTo(Nest other)
       {
            return Double.compare(score, other.score);
       }
       public Nest clone()
       {
           return new Nest(configuration.getAssignments());
       }
    }

    private Document currentDocument;

    private ConfigurationScorer configurationScorer;

    private Nest[] nests = new Nest[nestsNumber];
    
    public CuckooSearch(SimilarityMeasure similarityMeasure)
    {
        int threadsNumber = Runtime.getRuntime().availableProcessors();
        configurationScorer = new TverskyConfigurationScorer(similarityMeasure, threadsNumber);
    }

    public Configuration disambiguate(Document document)
    {
        currentDocument = document;

        for (int i = 0 ; i < nestsNumber ; i++)
        {
            nests[i] = new Nest();
        }
        
        for (int currentIteration = 0 ; currentIteration < iterationsNumber ; currentIteration++)
        {
            int progress = (int)(((double) currentIteration / (double) iterationsNumber) * 10000);
            System.out.println("Cuckoo progress : " + (double)progress / 100.0 + "%");
            
            int i = random.nextInt(nests.length);
            //System.out.println("Choosing nest " + i);
            Nest movedNestI = randomWalk(nests[i]);
            
            int j = random.nextInt(nests.length);
            //System.out.println("Choosing nest " + j);
            
            if (movedNestI.score > nests[j].score)
            {
                //System.out.println("Replacing " + j + " by " + i);
                //System.out.println("Score of " + j + " : " + nests[j].score);
                //System.out.println("Score of " + i + " : " + nests[i].score);
                nests[j] = movedNestI;
            }
            
            sortNests();
            abandonWorthlessNests();

            System.out.println("Current best : " + nests[nestsNumber - 1].score);
        }
        sortNests();
        return nests[nestsNumber - 1].configuration;
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
    
    private void sortNests()
    {
        Arrays.sort(nests);
    }
    
    private Nest randomWalk(Nest nest)
    {
        Nest clone = nest.clone();
        int distance = (int) levyDistribution.sample();
        System.out.println("Walking a distance of " + distance);
        clone.configuration.makeRandomChanges(distance);
        return clone;
    }

    private void abandonWorthlessNests()
    {
        for (int i = 0 ; i < destroyedNestsNumber ; i++)
        {
            nests[i] = new Nest();
        }
    }
}
