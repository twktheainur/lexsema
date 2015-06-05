package org.getalp.lexsema.wsd.parameters.bat;

import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionFactory;

public class BatParametersFactory implements CuckooSolutionFactory
{
    public CuckooSolution createRandomSolution()
    {
        return new BatParameters();
    }
}
