package org.getalp.optimization.functions.setfunctions.extentions;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.optimization.functions.Function;
import org.getalp.optimization.functions.input.FunctionInput;
import org.getalp.optimization.functions.setfunctions.SetFunction;
import org.getalp.optimization.functions.setfunctions.input.SetFunctionInput;
import org.getalp.optimization.methods.OptimisationMethod;

public interface Extension extends Function {
    public FunctionInput optimize(OptimisationMethod optimizationMethod, FunctionInput in);

    public double compute(SetFunctionInput in);

    public DoubleMatrix1D computeGradient(FunctionInput in);

    public boolean isDifferentiable();

    public void setSetFunction(SetFunction f);
}
