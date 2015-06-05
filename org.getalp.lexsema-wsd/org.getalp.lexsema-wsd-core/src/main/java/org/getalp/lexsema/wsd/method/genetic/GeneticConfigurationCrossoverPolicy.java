package org.getalp.lexsema.wsd.method.genetic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ChromosomePair;
import org.apache.commons.math3.genetics.CrossoverPolicy;

public class GeneticConfigurationCrossoverPolicy implements CrossoverPolicy
{
    public ChromosomePair crossover(Chromosome first, Chromosome second)
    {
        GeneticConfigurationChromosome c1 = (GeneticConfigurationChromosome) first;
        GeneticConfigurationChromosome c2 = (GeneticConfigurationChromosome) second;
        
        int[] new_c1_list = new int[c1.configuration.size()];
        int[] new_c2_list = new int[c2.configuration.size()];
        
        int begin = 0;
        int half = c1.configuration.size() / 2;
        int end = c2.configuration.size();
        
        for (int i = begin ; i < half ; i++)
        {
            new_c1_list[i] = c1.configuration.getAssignment(i);
            new_c2_list[i] = c2.configuration.getAssignment(i);
        }

        for (int i = half ; i < end ; i++)
        {
            new_c1_list[i] = c2.configuration.getAssignment(i);
            new_c2_list[i] = c1.configuration.getAssignment(i);
        }

        GeneticConfigurationChromosome new_c1 = new GeneticConfigurationChromosome(c1.configuration.getDocument(), new_c1_list, c1.scorer);
        GeneticConfigurationChromosome new_c2 = new GeneticConfigurationChromosome(c2.configuration.getDocument(), new_c2_list, c2.scorer);
        
        return new ChromosomePair(new_c1, new_c2);
    }
}
