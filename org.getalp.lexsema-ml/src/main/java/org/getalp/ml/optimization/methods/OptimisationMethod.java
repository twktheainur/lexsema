package org.getalp.ml.optimization.methods;

import org.getalp.ml.optimization.functions.Function;
import org.getalp.ml.optimization.functions.input.FunctionInput;

public interface OptimisationMethod {
    public FunctionInput optimise(FunctionInput input, Function f);
}
