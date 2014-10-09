package org.getalp.optimisation.functions;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.optimisation.functions.input.FunctionInput;

/**
 * Created by tchechem on 10/7/14.
 */
public interface Function {
    public  double F(FunctionInput input);
    public DoubleMatrix1D computeGradient(FunctionInput in);
    public boolean isDifferentiable();
}
