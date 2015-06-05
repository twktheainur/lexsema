package org.getalp.lexsema.wsd.parameters.annealing;

import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionFactory;

public class AnnealingParametersFactory implements CuckooSolutionFactory
{
    public CuckooSolution createRandomSolution()
    {
        return new AnnealingParameters();
    }

}
