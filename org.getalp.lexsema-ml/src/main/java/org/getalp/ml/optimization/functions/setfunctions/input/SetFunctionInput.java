package org.getalp.ml.optimization.functions.setfunctions.input;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.ml.optimization.functions.input.FunctionInput;
import org.getalp.ml.optimization.org.getalp.util.Vectors;


public abstract class SetFunctionInput implements FunctionInput {
    private DoubleMatrix1D input;
    private DoubleMatrix1D values;
    private DoubleMatrix1D permutation;

    private Interval interval;

    protected SetFunctionInput() {
    }

    public DoubleMatrix1D getInput() {
        return input;
    }

    public void setInput(DoubleMatrix1D input) {
        if (permutation == null) {
            permutation = Vectors.getDefaultPermutation(input);
        } else {
            permutation = Vectors.permutation(input);
        }
        interval = new Interval(0, (int) input.size());
        this.input = input;
    }

    public DoubleMatrix1D getValues() {
        return values;
    }

    protected void setValues(DoubleMatrix1D values) {
        this.values = values;
    }

    public DoubleMatrix1D getPermutation() {
        return permutation;
    }

    public void setPermutation(DoubleMatrix1D permutation) {
        this.permutation = permutation;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public void setInterval(int start, int end) {
        if (interval == null) {
            interval = new Interval(start, end);
        } else {
            interval.setStart(start);
            interval.setEnd(end);
        }
    }
}
