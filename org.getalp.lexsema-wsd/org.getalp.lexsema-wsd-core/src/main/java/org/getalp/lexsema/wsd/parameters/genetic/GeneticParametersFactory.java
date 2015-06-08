package org.getalp.lexsema.wsd.parameters.genetic;

import org.getalp.lexsema.wsd.parameters.method.Parameters;
import org.getalp.lexsema.wsd.parameters.method.ParametersFactory;

public class GeneticParametersFactory implements ParametersFactory
{
    public Parameters createRandomSolution()
    {
        return new GeneticParameters();
    }
}
