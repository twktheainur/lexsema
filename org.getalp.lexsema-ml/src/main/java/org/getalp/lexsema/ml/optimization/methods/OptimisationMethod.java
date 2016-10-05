package org.getalp.lexsema.ml.optimization.methods;

import org.getalp.lexsema.ml.optimization.functions.Function;
import org.getalp.lexsema.ml.optimization.functions.input.FunctionInput;

public interface OptimisationMethod {
    public FunctionInput optimise(FunctionInput input, Function f);
}
