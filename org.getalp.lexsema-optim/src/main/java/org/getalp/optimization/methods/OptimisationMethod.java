package org.getalp.optimization.methods;

import org.getalp.optimization.functions.Function;
import org.getalp.optimization.functions.input.FunctionInput;

public interface OptimisationMethod {
    public FunctionInput optimise(FunctionInput input, Function f);
}
