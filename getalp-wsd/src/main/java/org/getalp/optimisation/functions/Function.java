package org.getalp.optimisation.functions;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.optimisation.functions.input.FunctionInput;

public interface Function {
    public double F(FunctionInput input);

    public DoubleMatrix1D computeGradient(FunctionInput in);

    public boolean isDifferentiable();

    public void clearCache();
}
