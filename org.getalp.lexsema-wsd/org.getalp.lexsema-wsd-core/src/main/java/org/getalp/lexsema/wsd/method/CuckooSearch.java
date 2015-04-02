package org.getalp.lexsema.wsd.method;

import java.util.Arrays;
import java.util.Random;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.SemEval2007Task7PerfectConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;
import org.apache.commons.math3.distribution.LevyDistribution;

public class CuckooSearch implements Disambiguator
{
    private Random random = new Random();

    private int iterationsNumber;

    private LevyDistribution levyDistribution;
    
    private int nestsNumber;
    
    private int destroyedNestsNumber;

    private Document currentDocument;

    private ConfigurationScorer configurationScorer;

    private Nest[] nests;
    
    private class Nest implements Comparable<Nest>
    {
       public ContinuousConfiguration configuration;
       public double score;
       public Nest()
       {
           configuration = new ContinuousConfiguration(currentDocument);
           score = configurationScorer.computeScore(currentDocument, configuration);
       }
       public Nest(ContinuousConfiguration configuration, double score)
       {
           this.configuration = configuration.clone();
           this.score = score;
       }
       public int compareTo(Nest other)
       {
            return Double.compare(score, other.score);
       }
       public Nest clone()
       {
           return new Nest(configuration.clone(), score);
       }
       public void randomFly()
       {
           int distance = Math.min((int) levyDistribution.sample(), configuration.size());
           System.out.println("Flying a distance of " + distance);
           configuration.makeRandomChanges(distance);
           score = configurationScorer.computeScore(currentDocument, configuration);
       }
    }

    public CuckooSearch(int iterations, double levyScale, int nestsNumber, int destroyedNests,
                        SimilarityMeasure similarityMeasure)
    {
        iterationsNumber = iterations;
        levyDistribution = new LevyDistribution(1, levyScale);
        this.nestsNumber = nestsNumber;
        destroyedNestsNumber = destroyedNests;
        int threadsNumber = Runtime.getRuntime().availableProcessors();
        configurationScorer = new TverskyConfigurationScorer(similarityMeasure, threadsNumber);
        configurationScorer = new SemEval2007Task7PerfectConfigurationScorer("../data/senseval2007_task7/key/scorer2.sh");
        nests = new Nest[nestsNumber];
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
            nests[i].randomFly();
            
            int j = random.nextInt(nests.length);
            
            if (nests[i].score > nests[j].score)
            {
                nests[j] = nests[i].clone();
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

    private void abandonWorthlessNests()
    {
        for (int i = 0 ; i < destroyedNestsNumber ; i++)
        {
            nests[i] = new Nest();
        }
    }
}
