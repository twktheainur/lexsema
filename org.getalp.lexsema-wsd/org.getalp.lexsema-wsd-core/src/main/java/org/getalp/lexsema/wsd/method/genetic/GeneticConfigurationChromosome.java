package org.getalp.lexsema.wsd.method.genetic;

import java.util.List;

import org.apache.commons.math3.genetics.Chromosome;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class GeneticConfigurationChromosome extends Chromosome
{
    public ContinuousConfiguration configuration;
    
    public ConfigurationScorer scorer;

    public GeneticConfigurationChromosome(Document doc, ConfigurationScorer scorer)
    {
        configuration = new ContinuousConfiguration(doc);
        this.scorer = scorer;
    }
    
    public GeneticConfigurationChromosome(Document doc, int[] senses, ConfigurationScorer scorer)
    {
        configuration = new ContinuousConfiguration(doc, senses);
        this.scorer = scorer;
    }
    
    public double fitness()
    {
        return scorer.computeScore(configuration.getDocument(), configuration);
    }
    
    public GeneticConfigurationChromosome clone()
    {
        return new GeneticConfigurationChromosome(configuration.getDocument(), configuration.getAssignments(), scorer);
    }
}
