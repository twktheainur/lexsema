package org.getalp.lexsema.wsd.experiments.ga.wsd;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

public class GeneticConfigurationMutationPolicy implements MutationPolicy
{
    public Chromosome mutate(Chromosome original)
    {
        GeneticConfigurationChromosome conf = (GeneticConfigurationChromosome) original;
        GeneticConfigurationChromosome ret = conf.clone();
        ret.configuration.makeRandomChange();
        return ret;
    }
}
