package org.getalp.lexsema.wsd.experiments.cuckoo.parameters.genetic;

import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolutionFactory;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;

public class GeneticParametersFactory implements CuckooSolutionFactory
{
    public CuckooSolution createRandomSolution()
    {
        return new GeneticParameters();
    }

}
