package org.getalp.similarity.semantic.string.input;

import cern.colt.function.tdouble.DoubleFunction;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import com.wcohen.ss.AbstractStringDistance;
import org.getalp.optimization.functions.input.FunctionInput;
import org.getalp.optimization.functions.setfunctions.input.Interval;
import org.getalp.optimization.functions.setfunctions.input.SetFunctionInput;
import org.getalp.util.CommonSubsequences;

import java.util.List;

public class FuzzyCommonSubsequenceInput extends SetFunctionInput {

    private final AbstractStringDistance distance;
    private final List<String> la;
    private final List<String> lb;
    private boolean fuzzyMatching;
    private boolean invert;
    private DoubleFunction weightingFunction;


    public FuzzyCommonSubsequenceInput(List<String> la, List<String> lb,
                                       AbstractStringDistance distance,
                                       DoubleFunction weightingFunction,
                                       boolean fuzzyMatching, boolean invert) {
        this.la = la;
        this.lb = lb;
        this.distance = distance;
        this.fuzzyMatching = fuzzyMatching;
        this.invert = invert;
        this.weightingFunction = weightingFunction;
        compute();
    }

    public FuzzyCommonSubsequenceInput(List<String> la, List<String> lb,
                                       AbstractStringDistance distance, DoubleFunction weightingFunction,
                                       boolean fuzzyMatching, boolean invert, boolean compute) {
        this.la = la;
        this.lb = lb;
        this.distance = distance;
        this.fuzzyMatching = fuzzyMatching;
        this.invert = invert;
        this.weightingFunction = weightingFunction;
        if (compute) {
            compute();
        }
    }

    private void compute() {
        List<Double> common;
        CommonSubsequences commonSubsequences = new CommonSubsequences(la, lb);
        if (fuzzyMatching) {
            commonSubsequences.setFuzzyDistance(distance, .5d);
        }
        common = commonSubsequences.computeSubSequenceLengths();

        double maxsum = weightingFunction.apply((la.size() + lb.size()) / 2d);
        /*Applying quadratic weights and normalizing counts*/
        if (common.size() > 1) {
            for (int i = 0; i < common.size(); i++) {
                double value = common.get(i);
                common.set(i, weightingFunction.apply(value) / maxsum);
            }
        } else if (common.size() == 1) {
            common.set(0, weightingFunction.apply(common.get(0)));
        } else {
            common.add(0d);
        }

        setInput(new DenseDoubleMatrix1D(common.size()));
        setValues(new DenseDoubleMatrix1D(common.size()));
        for (int i = 0; i < common.size(); i++) {
            getInput().setQuick(i, 1d);
            double value = common.get(i);
            if (invert) {
                value = 1 - value;
            }
            getValues().setQuick(i, value);
        }
        setInterval(new Interval(0, (int) getInput().size()));
    }


    @Override
    public FunctionInput copy() {
        FuzzyCommonSubsequenceInput nin = new FuzzyCommonSubsequenceInput(la, lb, distance, weightingFunction, false, invert, false);
        nin.setInput(getInput().copy());
        nin.setValues(getValues().copy());
        if (getPermutation() != null) {
            nin.setPermutation(getPermutation());
        }
        return nin;
    }

}
