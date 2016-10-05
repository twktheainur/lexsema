package org.getalp.lexsema.ml.optimization.functions.setfunctions.input;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.lexsema.ml.vector.Vectors;


public abstract class AbstractSetFunctionInput implements SetFunctionInput {
    private DoubleMatrix1D input;
    private DoubleMatrix1D values;
    private DoubleMatrix1D permutation;

    private Interval interval;

    protected AbstractSetFunctionInput() {
    }

    @Override
    public DoubleMatrix1D getInput() {
        return input;
    }

    @Override
    public void setInput(DoubleMatrix1D input) {
        if (permutation == null) {
            permutation = Vectors.getDefaultPermutation(input);
        } else {
            permutation = Vectors.permutation(input);
        }
        interval = new Interval(0, (int) input.size());
        this.input = input;
    }

    @Override
    public DoubleMatrix1D getValues() {
        return values;
    }

    protected void setValues(DoubleMatrix1D values) {
        this.values = values;
    }

    @Override
    public DoubleMatrix1D getPermutation() {
        return permutation;
    }

    @Override
    public void setPermutation(DoubleMatrix1D permutation) {
        this.permutation = permutation;
    }

    @Override
    public Interval getInterval() {
        return interval;
    }

    @Override
    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    @Override
    public void setInterval(int start, int end) {
        if (interval == null) {
            interval = new Interval(start, end);
        } else {
            interval.setStart(start);
            interval.setEnd(end);
        }
    }
}
