package org.getalp.optimisation.methods;


import org.getalp.optimisation.functions.Function;
import org.getalp.optimisation.functions.input.FunctionInput;

public interface OptimisationMethod {
    public FunctionInput optimise(FunctionInput input, Function f);
}
