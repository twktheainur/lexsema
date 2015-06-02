package org.getalp.lexsema.wsd.experiments.cuckoo.parameters.annealing;

import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolutionFactory;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;

public class AnnealingParametersFactory implements CuckooSolutionFactory
{
    public CuckooSolution createRandomSolution()
    {
        return new AnnealingParameters();
    }

}
