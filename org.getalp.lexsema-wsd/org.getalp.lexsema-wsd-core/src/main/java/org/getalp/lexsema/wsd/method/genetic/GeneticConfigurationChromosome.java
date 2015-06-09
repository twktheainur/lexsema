package org.getalp.lexsema.wsd.method.genetic;

import org.apache.commons.math3.genetics.Chromosome;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class GeneticConfigurationChromosome extends Chromosome
{
    public ContinuousConfiguration configuration;
    
    public ConfigurationScorer scorer;
    
    public StopCondition stopCondition;

    public GeneticConfigurationChromosome(Document doc, ConfigurationScorer scorer, StopCondition stopCondition)
    {
        configuration = new ContinuousConfiguration(doc);
        this.scorer = scorer;
        this.stopCondition = stopCondition;
    }
    
    public GeneticConfigurationChromosome(Document doc, int[] senses, ConfigurationScorer scorer, StopCondition stopCondition)
    {
        configuration = new ContinuousConfiguration(doc, senses);
        this.scorer = scorer;
        this.stopCondition = stopCondition;
    }
    
    public double fitness()
    {
        stopCondition.incrementScorerCalls();
        return scorer.computeScore(configuration.getDocument(), configuration);
    }
    
    public GeneticConfigurationChromosome clone()
    {
        return new GeneticConfigurationChromosome(configuration.getDocument(), configuration.getAssignments(), scorer, stopCondition);
    }
}
