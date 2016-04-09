package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;

public class ConfigurationScorerWithCache implements ConfigurationScorer
{
    private SimilarityMeasure similarityMeasure;
    
    private double[][][][] cache;
    
    private Document currentDocument;

    private boolean verbose;
    
    public ConfigurationScorerWithCache(SimilarityMeasure similarityMeasure)
    {
        this(similarityMeasure, false);
    }

    public ConfigurationScorerWithCache(SimilarityMeasure similarityMeasure, boolean verbose)
    {
        this.similarityMeasure = similarityMeasure;
        this.verbose = verbose;
        cache = null;
        currentDocument = null;
    }

    public double computeScore(Document d, Configuration c)
    {
        if (currentDocument != d)
        {
            cache = new double[d.size()][d.size()][][];
            for (int i = 0 ; i < d.size() ; i++)
            {
                for (int j = i + 1 ; j < d.size() ; j++)
                {
                    cache[i][j] = new double[d.getSenses(i).size()][d.getSenses(j).size()];
                    for (int k = 0 ; k < d.getSenses(i).size() ; k++)
                    {
                        for (int l = 0 ; l < d.getSenses(j).size() ; l++)
                        {
                            cache[i][j][k][l] = -1;
                        }
                    }
                }
            }
            if (verbose)
            {
                long size = 0;
                for (int i = 0 ; i < cache.length ; i++) {
                    for (int j = 0 ; j < cache[i].length ; j++) {
                        for (int k = 0 ; k < cache[i][j].length ; k++) {
                            size += cache[i][j][k].length;
                        }
                    }
                }
                System.out.println("Initialized cache of size " + size);
            }
            currentDocument = d;
        }
        
        double totalScore = 0;
        for (int i = 0 ; i < c.size() ; i++)
        {
            if (verbose) {
                System.out.println("Scorer : " + (i+1) + "/" + c.size());
            }
            double score = 0;
            int k = c.getAssignment(i);
            if (k < 0 || d.getSenses(i).isEmpty()) continue;
            Sense senseA = d.getSenses(i).get(k);
            for (int j = i + 1 ; j < c.size() ; j++)
            {
                int l = c.getAssignment(j);
                if (l < 0 || d.getSenses(j).isEmpty()) continue;
                double cacheCell = cache[i][j][k][l];
                if (cacheCell != -1)
                {
                    score += cacheCell;
                }
                else
                {
                    Sense senseB = d.getSenses(j).get(l);
                    double similarity = senseA.computeSimilarityWith(similarityMeasure, senseB);
                    score += similarity;
                    cache[i][j][k][l] = similarity;
                }
            }
            totalScore += score;
        }
        return totalScore;
    }

    public void release()
    {

    }
}
