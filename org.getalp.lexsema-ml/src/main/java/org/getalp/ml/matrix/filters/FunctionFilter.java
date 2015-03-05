package org.getalp.ml.matrix.filters;

import cern.colt.function.tdouble.DoubleFunction;
import cern.colt.matrix.tdouble.DoubleMatrix2D;

public class FunctionFilter implements Filter {

    DoubleFunction function;

    private boolean enabled;

    public FunctionFilter(DoubleFunction function) {
        this.function = function;
    }

    @Override
    public DoubleMatrix2D apply(DoubleMatrix2D signal) {
        if (!enabled) {
            return null;
        }
        signal.assign(function);
        return signal;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
