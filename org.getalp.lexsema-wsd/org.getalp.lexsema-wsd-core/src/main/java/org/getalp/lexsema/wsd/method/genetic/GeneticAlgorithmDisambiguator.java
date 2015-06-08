package org.getalp.lexsema.wsd.method.genetic;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedElapsedTime;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.method.IterationStopCondition;
import org.getalp.lexsema.wsd.method.StopCondition;
import org.getalp.lexsema.wsd.method.TimeStopCondition;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class GeneticAlgorithmDisambiguator implements Disambiguator
{
    private StopCondition stopCondition;
    
    private int population;
    
    private double crossoverRate;
    
    private double mutationRate;

    private ConfigurationScorer scorer;
    
    public GeneticAlgorithmDisambiguator(StopCondition stopCondition, int population, double crossoverRate, double mutationRate, ConfigurationScorer scorer)
    {
        this.stopCondition = stopCondition;
        this.population = population;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.scorer = scorer;
    }
    
    public Configuration disambiguate(Document document)
    {
        StoppingCondition stopCondition = null;
        if (this.stopCondition instanceof TimeStopCondition)
        {
            stopCondition = new FixedElapsedTime(((TimeStopCondition)this.stopCondition).getMilliSeconds(), TimeUnit.MILLISECONDS);
        }
        else if (this.stopCondition instanceof IterationStopCondition)
        {
            stopCondition = new FixedGenerationCount(((IterationStopCondition)this.stopCondition).getIterationsNumber());
        }
        
        Population population = new ElitisticListPopulation(this.population, 0.2);
        for (int i = 0 ; i < this.population ; i++)
        {
            population.addChromosome(new GeneticConfigurationChromosome(document, scorer));
        }
        
        GeneticConfigurationCrossoverPolicy crossoverPolicy = new GeneticConfigurationCrossoverPolicy();
        GeneticConfigurationMutationPolicy mutationPolicy = new GeneticConfigurationMutationPolicy();
        TournamentSelection selectionPolicy = new TournamentSelection(2);
        
        GeneticAlgorithm ga = new GeneticAlgorithm(crossoverPolicy, crossoverRate, mutationPolicy, mutationRate, selectionPolicy);
        
        population = ga.evolve(population, stopCondition);
        
        return ((GeneticConfigurationChromosome) population.getFittestChromosome()).configuration;
    }

    public Configuration disambiguate(Document document, Configuration c)
    {
        return disambiguate(document);
    }

    public void release()
    {
        
    }
}
