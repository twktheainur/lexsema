package org.getalp.lexsema.ml.optimization.functions.input;

import cern.colt.matrix.tdouble.DoubleMatrix1D;

public interface FunctionInput {
    public DoubleMatrix1D getInput();

    public void setInput(DoubleMatrix1D input);

    public FunctionInput copy();
}
