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
import org.apache.commons.math3.distribution.NormalDistribution;

public class CuckooSearch implements Disambiguator
{
    private static final Random random = new Random();
    
    private static final LevyDistribution levyDistribution = new LevyDistribution(0, 0.5);
    
    private static final NormalDistribution normalDistribution = new NormalDistribution();

    private static final int nestsNumber = 15;
    
    private static final int destroyedNestsNumber = 5;

    private static final int iterationsNumber = 200;

    private class Nest implements Comparable<Nest>
    {
       public ContinuousConfiguration configuration;
       public double score;
       public Nest()
       {
           configuration = new ContinuousConfiguration(currentDocument);
           score = configurationScorer.computeScore(currentDocument, configuration);
       }
       public Nest(double[] position)
       {
           configuration = new ContinuousConfiguration(currentDocument, position);
           score = configurationScorer.computeScore(currentDocument, configuration);
       }
       public void setPosition(double[] position)
       {
           configuration.setSenses(position);
           score = configurationScorer.computeScore(currentDocument, configuration);
       }
       public int compareTo(Nest other)
       {
            return Double.compare(score, other.score);
       }
       public Nest clone()
       {
           Nest ret = new Nest();
           ret.setPosition(configuration.getAssignmentsAsDouble());
           return ret;
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
        double[] position = nest.configuration.getAssignmentsAsDouble().clone();

        double distance = levyDistribution.sample();
        //System.out.println("Walking a distance of " + distance);
        
        double[] direction = randomDirection(position.length);
        
        for (int i = 0 ; i < position.length ; i++)
        {
            //System.out.println("Movement " + i + " : " + direction[i] * distance);
            position[i] += direction[i] * distance;
        }
        
        return new Nest(position);
    }
    
    private double[] randomDirection(int dimension)
    {
        double[] ret = new double[dimension];
        double tmp = 0;

        for (int i = 0 ; i < dimension ; i++)
        {
            ret[i] = normalDistribution.sample();
            tmp = Math.max(tmp, Math.abs(ret[i]));
            // tmp += ret[i] * ret[i]; // using norm-2
        }
        if (tmp == 0) return randomDirection(dimension);
        // tmp = Math.sqrt(tmp); // using norm-2
        for (int i = 0 ; i < dimension ; i++)
        {
            ret[i] /= tmp;
        }
        
        return ret;
    }
    
    private void abandonWorthlessNests()
    {
        for (int i = 0 ; i < destroyedNestsNumber ; i++)
        {
            nests[i] = new Nest();
        }
    }
}
