package org.getalp.optimisation.functions.setfunctions.submodular.extentions;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.optimisation.functions.Function;
import org.getalp.optimisation.functions.input.FunctionInput;
import org.getalp.optimisation.functions.setfunctions.SetFunction;
import org.getalp.optimisation.functions.setfunctions.input.SetFunctionInput;
import org.getalp.optimisation.methods.OptimisationMethod;

public interface Extension extends Function {
    public FunctionInput optimise(OptimisationMethod optimizationMethod, FunctionInput in);
    public double compute(SetFunctionInput in);
    public DoubleMatrix1D computeGradient(FunctionInput in);
    public boolean isDifferentiable();
    public void setSetFunction(SetFunction f);
}
