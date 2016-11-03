package org.getalp.lexsema.wsd.method.genetic;

import org.apache.commons.math3.genetics.Chromosome;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.ContinuousConfiguration;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

import java.io.PrintWriter;

public class GeneticConfigurationChromosome extends Chromosome
{
    public ContinuousConfiguration configuration;
    
    public ConfigurationScorer scorer;
    
    public StopCondition stopCondition;
    
    public PrintWriter plotWriter = null;
    
    public GeneticConfigurationChromosome(Document doc, ConfigurationScorer scorer, StopCondition stopCondition, PrintWriter plotWriter)
    {
        configuration = new ContinuousConfiguration(doc);
        this.scorer = scorer;
        this.stopCondition = stopCondition;
        this.plotWriter = plotWriter;
    }
    
    public GeneticConfigurationChromosome(Document doc, int[] senses, ConfigurationScorer scorer, StopCondition stopCondition, PrintWriter plotWriter)
    {
        configuration = new ContinuousConfiguration(doc, senses);
        this.scorer = scorer;
        this.stopCondition = stopCondition;
        this.plotWriter = plotWriter;
    }
    
    public double fitness()
    {
		double score = scorer.computeScore(configuration.getDocument(), configuration);
		stopCondition.incrementScorerCalls();
		if (plotWriter != null) plotWriter.println(stopCondition.getCurrent() + " " + GeneticAlgorithmDisambiguator.bestScore);
        return score;
    }
    
    public GeneticConfigurationChromosome clone()
    {
        return new GeneticConfigurationChromosome(configuration.getDocument(), configuration.getAssignments(), scorer, stopCondition, plotWriter);
    }
}
