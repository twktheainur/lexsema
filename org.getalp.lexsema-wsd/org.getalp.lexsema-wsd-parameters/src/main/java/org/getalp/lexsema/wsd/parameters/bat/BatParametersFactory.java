package org.getalp.lexsema.wsd.parameters.bat;

import org.getalp.lexsema.wsd.parameters.method.Parameters;
import org.getalp.lexsema.wsd.parameters.method.ParametersFactory;

public class BatParametersFactory implements ParametersFactory
{
    public Parameters createRandomSolution()
    {
        return new BatParameters();
    }
}
