package org.getalp.ml.optimization.functions.setfunctions.input;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.ml.optimization.functions.input.FunctionInput;

import java.util.List;

public class ValueListInput extends AbstractSetFunctionInput {

    private List<Double> values;
    private boolean invert;

    public ValueListInput(List<Double> values, boolean invert) {
        this.values = values;
        this.invert = invert;
        compute();
    }

    public ValueListInput(List<Double> values, boolean invert, boolean compute) {
        this.values = values;
        this.invert = invert;
        if (compute) {
            compute();
        }
    }

    private void compute() {
        setInput(new DenseDoubleMatrix1D(values.size()));
        setValues(new DenseDoubleMatrix1D(values.size()));
        for (int i = 0; i < values.size(); i++) {
            getInput().setQuick(i, 1d);
            if (invert) {
                getValues().setQuick(i, 1 - values.get(i));
            } else {
                getValues().setQuick(i, values.get(i));
            }
        }
        setInterval(new Interval(0, (int) getInput().size()));
    }


    @Override
    public FunctionInput copy() {
        ValueListInput nin = new ValueListInput(values, invert, false);
        nin.setInput(getInput().copy());
        nin.setValues(getValues().copy());
        if (getPermutation() != null) {
            nin.setPermutation(getPermutation());
        }
        return nin;
    }

}
