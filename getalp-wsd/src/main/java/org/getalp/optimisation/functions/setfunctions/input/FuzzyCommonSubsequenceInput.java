package org.getalp.optimisation.functions.setfunctions.input;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import com.wcohen.ss.AbstractStringDistance;
import org.getalp.optimisation.functions.input.FunctionInput;
import org.getalp.util.SubSequences;

import java.util.List;

public class FuzzyCommonSubsequenceInput extends SetFunctionInput {

    private final AbstractStringDistance distance;
    private final List<String> la;
    private final List<String> lb;
    private boolean fuzzyMatching;


    public FuzzyCommonSubsequenceInput(List<String> la, List<String> lb, AbstractStringDistance distance, boolean fuzzyMatching) {
        this.la = la;
        this.lb = lb;
        this.distance = distance;
        this.fuzzyMatching = fuzzyMatching;
        compute();
    }

    public FuzzyCommonSubsequenceInput(List<String> la, List<String> lb, AbstractStringDistance distance, boolean fuzzyMatching, boolean compute) {
        this.la = la;
        this.lb = lb;
        this.distance = distance;
        this.fuzzyMatching = fuzzyMatching;
        if (compute) {
            compute();
        }
    }

    private void compute() {
        List<Double> common;
        if (fuzzyMatching) {
            common = SubSequences.fuzzyLongestCommonSubSequences(la, lb, distance, 0.5);
        } else {
            common = SubSequences.longestCommonSubSequences(la, lb);
        }
        setInput(new DenseDoubleMatrix1D(common.size()));
        setValues(new DenseDoubleMatrix1D(common.size()));
        for (int i = 0; i < common.size(); i++) {
            getInput().setQuick(i, 1d);
            getValues().setQuick(i, common.get(i));
        }
        setInterval(new Interval(0, (int) getInput().size()));
    }


    @Override
    public FunctionInput copy() {
        FuzzyCommonSubsequenceInput nin = new FuzzyCommonSubsequenceInput(la, lb, distance, false);
        nin.setInput(getInput().copy());
        nin.setValues(getValues().copy());
        if (getPermutation() != null) {
            nin.setPermutation(getPermutation());
        }
        return nin;
    }

}
