package org.getalp.lexsema.wsd.method;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.distribution.LevyDistribution;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class CuckooSearchDisambiguator implements Disambiguator
{
    public PrintWriter scorePlotWriter = null;

    public PrintWriter perfectScorePlotWriter = null;
    
    public ConfigurationScorer perfectScorer = null;
    
    private class Nest implements Comparable<Nest>
    {
        public ContinuousConfiguration configuration;
        
        private double score;
        
        private boolean needRecomputeScore;
        
        public Nest()
        {
            this.configuration = new ContinuousConfiguration(currentDocument);
            this.score = 0;
            this.needRecomputeScore = true;
        }
        
        public Nest(ContinuousConfiguration configuration, double score)
        {
            this.configuration = configuration;
            this.score = score;
            needRecomputeScore = false;
        }
        
        public int compareTo(Nest other)
        {
            return Double.compare(getScore(), other.getScore());
        }
        
        public Nest clone()
        {
            return new Nest(configuration.clone(), getScore());
        }
        
        public double randomFly()
        {
            double distance = levyDistribution.sample();
            configuration.makeRandomChanges((int) distance);
            needRecomputeScore = true;
            return distance;
        }
        
        public double getScore()
        {
            if (needRecomputeScore)
            {
                score = configurationScorer.computeScore(currentDocument, configuration);
                needRecomputeScore = false;
                stopCondition.incrementScorerCalls();
                if (scorePlotWriter != null) scorePlotWriter.println(stopCondition.getCurrent() + " " + (nests[0] != null ? nests[0].getScore() : 0));
                if (perfectScorePlotWriter != null && perfectScorer != null) perfectScorePlotWriter.println(stopCondition.getCurrent() + " " + perfectScorer.computeScore(currentDocument, nests[0].configuration));
            }
            return score;
        }
    }

    private static final Random random = new Random();
    
    private StopCondition stopCondition;

    private LevyDistribution levyDistribution;
    
    private int nestsNumber;
    
    private int destroyedNestsNumber;

    private ConfigurationScorer configurationScorer;

    private Nest[] nests;
    
    private boolean verbose;
    
    private Document currentDocument;

    public CuckooSearchDisambiguator(int iterations, double levyLocation, double levyScale, int nestsNumber, int destroyedNests, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this(new StopCondition(StopCondition.Condition.ITERATIONS, iterations), levyLocation, levyScale, nestsNumber, destroyedNests, configurationScorer, verbose);
    }

    public CuckooSearchDisambiguator(StopCondition stopCondition, double levyLocation, double levyScale, int nestsNumber, int destroyedNests, ConfigurationScorer configurationScorer, boolean verbose)
    {
        this.stopCondition = stopCondition;
        this.levyDistribution = new LevyDistribution(levyLocation, levyScale);
        this.nestsNumber = nestsNumber;
        this.destroyedNestsNumber = destroyedNests;
        this.configurationScorer = configurationScorer;
        nests = new Nest[nestsNumber];
        this.verbose = verbose;
    }

    public Configuration disambiguate(Document document)
    {
        this.currentDocument = document;
        Configuration ret = null;
        if (nestsNumber == 1) ret = runWithSingleNest(document);
        else ret = runWithManyNests(document);
        if (scorePlotWriter != null) scorePlotWriter.flush();
        if (perfectScorePlotWriter != null) perfectScorePlotWriter.flush();
        return ret;
    }
    
    private Configuration runWithSingleNest(Document document)
    {
        stopCondition.reset();
        nests[0] = new Nest();
        
        while (!stopCondition.stop())
        {
            int progress = (int)(stopCondition.getProgressPercentage() * 100);
            double progressPercent = (double)progress / 100.0;
            Nest newNest = nests[0].clone();
            double distance = newNest.randomFly();
            if (newNest.getScore() > nests[0].getScore())
            {
                nests[0] = newNest;
            }
            if (verbose)
            {
                System.out.println("Cuckoo Progress : " + progressPercent + "% - " +
                                   "Current best : " + nests[0].getScore() + " - " + 
                		           "Distance flight : " + distance);
            }
            stopCondition.incrementIterations();
            stopCondition.updateMilliseconds();
        }
        return nests[0].configuration;
    }
    
    private Configuration runWithManyNests(Document document)
    {
        stopCondition.reset();
        
        for (int i = 0 ; i < nestsNumber ; i++)
        {
            nests[i] = new Nest();
        }
        
        while (!stopCondition.stop())
        {
            int progress = (int)(stopCondition.getProgressPercentage() * 100);
            double progressPercent = (double)progress / 100.0;
            
            int i = random.nextInt(nests.length);
            Nest new_i = nests[i].clone();
            new_i.randomFly();
            
            int j = random.nextInt(nests.length);
            while (j == i) j = random.nextInt(nests.length);
            
            if (new_i.getScore() > nests[j].getScore())
            {
                nests[j] = new_i;
            }
            
            sortNests();
            abandonWorthlessNests();

            if (verbose)
            {
                System.out.println("Cuckoo Progress : " + progressPercent + "% - " +
                                   "Current best : " + nests[nestsNumber - 1].getScore() + 
                                   " [" + nests[nestsNumber - 1].configuration + "]");
            }
            stopCondition.incrementIterations();
            stopCondition.updateMilliseconds();
            if (scorePlotWriter != null)  scorePlotWriter.println(stopCondition.getCurrent() + " " + nests[nestsNumber - 1].getScore());
            if (perfectScorePlotWriter != null && perfectScorer != null) perfectScorePlotWriter.println(stopCondition.getCurrent() + " " + perfectScorer.computeScore(document, nests[nestsNumber - 1].configuration));
        }
        sortNests();
        return nests[nestsNumber - 1].configuration;
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

    public Configuration disambiguate(Document document, Configuration c)
    {
        return disambiguate(document);
    }
    
    public void release()
    {
        configurationScorer.release();
    }
}
