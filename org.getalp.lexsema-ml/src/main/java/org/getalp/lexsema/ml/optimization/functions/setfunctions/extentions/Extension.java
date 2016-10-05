package org.getalp.lexsema.ml.optimization.functions.setfunctions.extentions;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.lexsema.ml.optimization.functions.Function;
import org.getalp.lexsema.ml.optimization.functions.setfunctions.SetFunction;
import org.getalp.lexsema.ml.optimization.functions.setfunctions.input.SetFunctionInput;
import org.getalp.lexsema.ml.optimization.methods.OptimisationMethod;
import org.getalp.lexsema.ml.optimization.functions.input.FunctionInput;

public interface Extension extends Function {
    public FunctionInput optimize(OptimisationMethod optimizationMethod, FunctionInput in);

    public double compute(SetFunctionInput in);

    public DoubleMatrix1D computeGradient(FunctionInput in);

    public boolean isDifferentiable();

    public void setSetFunction(SetFunction f);
}
