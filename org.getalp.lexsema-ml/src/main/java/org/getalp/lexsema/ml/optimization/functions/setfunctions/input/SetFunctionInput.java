package org.getalp.lexsema.ml.optimization.functions.setfunctions.input;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.lexsema.ml.optimization.functions.input.FunctionInput;

/**
 * Input for a set function, that unlike a regular function can be
 * be ordered following a permutation vector.
 */
public interface SetFunctionInput extends FunctionInput {
    DoubleMatrix1D getValues();

    DoubleMatrix1D getPermutation();

    void setPermutation(DoubleMatrix1D permutation);

    Interval getInterval();

    void setInterval(Interval interval);

    void setInterval(int start, int end);
}
