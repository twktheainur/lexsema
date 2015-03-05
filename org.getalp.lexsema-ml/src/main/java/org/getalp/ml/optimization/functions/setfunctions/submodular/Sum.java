package org.getalp.ml.optimization.functions.setfunctions.submodular;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.ml.optimization.functions.cache.SetFunctionCache;
import org.getalp.ml.optimization.functions.input.FunctionInput;
import org.getalp.ml.optimization.functions.setfunctions.SetFunction;
import org.getalp.ml.optimization.functions.setfunctions.input.SetFunctionInput;


public class Sum extends SetFunction {
    private SetFunctionCache cache;
    private double multiplier;

    public Sum(double multiplier) {
        super();
        this.multiplier = multiplier;
        cache = new SetFunctionCache();
    }

    @Override
    public double F(SetFunctionInput input) {
        DoubleMatrix1D inputVect = input.getInput();
        DoubleMatrix1D permutation = input.getPermutation();
        DoubleMatrix1D values = input.getValues();
        int start = input.getInterval().getStart();
        int end = input.getInterval().getEnd();
        Double cacheval = cache.get(start, end);
        if (cacheval != null) {
            return cacheval;
        }
        double sum = 0d;
        for (int i = start; i < end && i < values.size(); i++) {
            sum += inputVect.get((int)
                    permutation.get(i)) *
                    values.get((int) permutation.get(i));
        }
        cache.put(start, end, sum);
        return multiplier * sum;
    }

    @Override
    public DoubleMatrix1D computeGradient(FunctionInput input) {
        DoubleMatrix1D ret = null;
        if (getExtension() != null) {
            ret = getExtension().computeGradient(input);
        }
        return ret;
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
