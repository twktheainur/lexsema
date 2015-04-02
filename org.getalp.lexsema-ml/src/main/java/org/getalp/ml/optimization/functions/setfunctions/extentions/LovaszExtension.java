package org.getalp.ml.optimization.functions.setfunctions.extentions;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.ml.optimization.functions.input.FunctionInput;
import org.getalp.ml.optimization.functions.setfunctions.SetFunction;
import org.getalp.ml.optimization.functions.setfunctions.input.SetFunctionInput;
import org.getalp.ml.optimization.methods.OptimisationMethod;
import org.getalp.ml.optimization.org.getalp.util.Vectors;

public class LovaszExtension implements Extension {

    private SetFunction function;

    public LovaszExtension() {
    }


    @Override
    public double F(FunctionInput input) {
        return compute((SetFunctionInput) input);
    }

    @Override
    public FunctionInput optimize(OptimisationMethod optimizationMethod, FunctionInput input) {
        return optimizationMethod.optimise(input, function);
    }

    @Override
    public double compute(SetFunctionInput in) {
        in.setPermutation(Vectors.permutation(in.getInput()));
        double lovaszScore = 0;
        for (int i = 0; i < in.getInput().size(); i++) {

            double wval_c = in.getInput().get((int) in.getPermutation().get(i));
            double wval_n;
            if (i < in.getInput().size() - 1) {
                wval_n = in.getInput().get((int) in.getPermutation().get(i + 1));
            } else {
                wval_n = 0;
            }
            in.setInterval(0, i);
            double localScore = (wval_c - wval_n) * function.F(in);
            lovaszScore += localScore;

        }
        return lovaszScore;
    }

    @Override
    public DoubleMatrix1D computeGradient(FunctionInput in) {
        SetFunctionInput sin = (SetFunctionInput) in;
        DoubleMatrix1D gradient = new DenseDoubleMatrix1D((int) in.getInput().size());
        sin.setPermutation(Vectors.permutation(in.getInput()));
        sin.setInterval(0, 1);
        gradient.setQuick(0, function.F(sin));
        for (int i = 1; i < in.getInput().size() - 1; i++) {
            sin.setInterval(0, i);
            double evalPrev = function.F(sin);
            sin.setInterval(0, i + 1);
            double evalCurr = function.F(sin);
            double diff = evalCurr - evalPrev;
            gradient.setQuick(i, diff);
        }
        return gradient;
    }

    @Override
    public boolean isDifferentiable() {
        return true;
    }

    @Override
    public void clearCache() {

    }

    @Override
    public void setSetFunction(SetFunction f) {
        function = f;
    }


}
