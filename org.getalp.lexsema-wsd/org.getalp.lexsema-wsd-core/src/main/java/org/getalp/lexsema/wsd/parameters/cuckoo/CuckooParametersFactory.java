package org.getalp.lexsema.wsd.parameters.cuckoo;

import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolution;
import org.getalp.lexsema.wsd.method.cuckoo.generic.CuckooSolutionFactory;

public class CuckooParametersFactory implements CuckooSolutionFactory
{
    public CuckooSolution createRandomSolution()
    {
        return new CuckooParameters();
    }

}
