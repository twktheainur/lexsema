package org.getalp.lexsema.wsd.experiments.cuckoo.parameters;

import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolutionFactory;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;

public class CuckooParametersFactory implements CuckooSolutionFactory
{
    public CuckooSolution createRandomSolution()
    {
        return new CuckooParameters();
    }

}
