package org.getalp.lexsema.wsd.parameters.annealing;

import org.getalp.lexsema.wsd.parameters.method.Parameters;
import org.getalp.lexsema.wsd.parameters.method.ParametersFactory;

public class AnnealingParametersFactory implements ParametersFactory
{
    public Parameters createRandomSolution()
    {
        return new AnnealingParameters();
    }
}
