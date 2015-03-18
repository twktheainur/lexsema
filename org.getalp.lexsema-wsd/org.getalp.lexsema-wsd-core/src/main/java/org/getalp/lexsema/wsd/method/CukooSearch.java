package org.getalp.lexsema.wsd.method;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.BatConfiguration;
import org.getalp.lexsema.wsd.configuration.ConfidenceConfiguration;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;
import org.getalp.lexsema.wsd.score.TverskyConfigurationScorer;

import org.apache.commons.math3.distribution.LevyDistribution;;

public class CukooSearch implements Disambiguator
{
    private static final Random random = new Random();
    
    private static final LevyDistribution levyDistribution = new LevyDistribution(0, 1);

    private static final int nestsNumber = 10;

    private static final int iterationsNumber = 10;

    private Document currentDocument;

    private ConfigurationScorer configurationScorer;

    private List<BatConfiguration> configurations = new ArrayList<BatConfiguration>();
    
    private Configuration bestConfiguration;
    
    public CukooSearch(SimilarityMeasure similarityMeasure)
    {
        int threadsNumber = Runtime.getRuntime().availableProcessors();
        configurationScorer = new TverskyConfigurationScorer(similarityMeasure, threadsNumber);
    }

    @Override
    public Configuration disambiguate(Document document)
    {
        currentDocument = document;

        for (int i = 0 ; i < nestsNumber ; i++)
        {
            configurations.add(new BatConfiguration(currentDocument));
        }
        
        updateBestConfiguration();
        
        for (int currentIteration = 0 ; currentIteration < iterationsNumber ; currentIteration++)
        {
            BatConfiguration configI = configurations.get(random.nextInt(configurations.size()));
            int[] positionI = configI.getAssignments();
            positionI = randomWalk(positionI);
            configI.setSenses(positionI);
            double scoreI = configurationScorer.computeScore(currentDocument, configI);
            
            BatConfiguration configJ = configurations.get(random.nextInt(configurations.size()));
            double scoreJ = configurationScorer.computeScore(currentDocument, configJ);
            
            if (scoreI > scoreJ)
            {
                // put
            }
        }
        return bestConfiguration;
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
    
    private void updateBestConfiguration()
    {
        double bestScore = Double.MIN_VALUE;
        for (Configuration currentConfiguration : configurations)
        {
            double currentScore = configurationScorer.computeScore(currentDocument, currentConfiguration);
            if (currentScore > bestScore)
            {
                bestScore = currentScore;
                bestConfiguration = currentConfiguration;
            }
        }
    }
    
    private int[] randomWalk(int[] position)
    {
        int[] newPosition = position.clone();

        double distance = levyDistribution.sample();
        
        return newPosition;
    }
}
