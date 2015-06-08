package org.getalp.lexsema.wsd.parameters.cuckoo;

import org.getalp.lexsema.wsd.parameters.method.Parameters;
import org.getalp.lexsema.wsd.parameters.method.ParametersFactory;

public class CuckooParametersFactory implements ParametersFactory
{
    public Parameters createRandomSolution()
    {
        return new CuckooParameters();
    }
}
