package org.getalp.lexsema.wsd.parameters.genetic;

import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionFactory;

public class GeneticParametersFactory implements CuckooSolutionFactory
{
    public CuckooSolution createRandomSolution()
    {
        return new GeneticParameters();
    }

}
