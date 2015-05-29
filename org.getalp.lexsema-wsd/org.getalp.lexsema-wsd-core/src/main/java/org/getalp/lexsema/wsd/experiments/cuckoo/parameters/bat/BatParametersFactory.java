package org.getalp.lexsema.wsd.experiments.cuckoo.parameters.bat;

import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.experiments.cuckoo.generic.CuckooSolutionFactory;

public class BatParametersFactory implements CuckooSolutionFactory
{
    public CuckooSolution createRandomSolution()
    {
        return new BatParameters();
    }
}
