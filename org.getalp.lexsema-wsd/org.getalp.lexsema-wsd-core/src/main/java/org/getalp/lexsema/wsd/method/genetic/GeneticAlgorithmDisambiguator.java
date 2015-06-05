package org.getalp.lexsema.wsd.method.genetic;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.getalp.lexsema.wsd.score.ConfigurationScorer;

public class GeneticAlgorithmDisambiguator implements Disambiguator
{
    private int iterations;

    private int population;
    
    private double crossoverRate;
    
    private double mutationRate;

    private ConfigurationScorer scorer;
    
    public GeneticAlgorithmDisambiguator(int iterations, int population, double crossoverRate, double mutationRate, ConfigurationScorer scorer)
    {
        this.iterations = iterations;
        this.population = population;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.scorer = scorer;
    }
    
    public Configuration disambiguate(Document document)
    {
        Population population = new ElitisticListPopulation(this.population, 0.2);
        for (int i = 0 ; i < this.population ; i++)
        {
            population.addChromosome(new GeneticConfigurationChromosome(document, scorer));
        }
        
        GeneticConfigurationCrossoverPolicy crossoverPolicy = new GeneticConfigurationCrossoverPolicy();
        GeneticConfigurationMutationPolicy mutationPolicy = new GeneticConfigurationMutationPolicy();
        TournamentSelection selectionPolicy = new TournamentSelection(2);
        
        GeneticAlgorithm ga = new GeneticAlgorithm(crossoverPolicy, crossoverRate, mutationPolicy, mutationRate, selectionPolicy);
        
        population = ga.evolve(population, new FixedGenerationCount(iterations));
        
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
