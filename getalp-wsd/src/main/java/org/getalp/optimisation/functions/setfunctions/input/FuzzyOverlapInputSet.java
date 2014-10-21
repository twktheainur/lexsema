package org.getalp.optimisation.functions.setfunctions.input;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import com.wcohen.ss.AbstractStringDistance;
import org.getalp.optimisation.functions.input.FunctionInput;

import java.util.List;

public class FuzzyOverlapInputSet extends SetFunctionInput {

    private final AbstractStringDistance distance;
    private List<String> la;
    private List<String> lb;
    private List<Double> weightsA;
    private List<Double> weightsB;
    private boolean invert;

    public FuzzyOverlapInputSet(List<String> la, List<String> lb, List<Double> weightsA, List<Double> weightsB, AbstractStringDistance distance, boolean invert) {
        this.la = la;
        this.lb = lb;
        this.weightsA = weightsA;
        this.weightsB = weightsB;
        this.distance = distance;
        this.invert = invert;
        compute();
    }

    public FuzzyOverlapInputSet(List<String> la, List<String> lb, List<Double> weightsA, List<Double> weightsB, AbstractStringDistance distance, boolean invert, boolean compute) {
        this.la = la;
        this.lb = lb;
        this.weightsA = weightsA;
        this.weightsB = weightsB;
        this.distance = distance;
        this.invert = invert;
        if (compute) {
            compute();
        }
    }

    private void compute() {
        setInput(new DenseDoubleMatrix1D(la.size() * lb.size()));
        setValues(new DenseDoubleMatrix1D(la.size() * lb.size()));
        for (int i = 0; i < la.size(); i++) {
            String a = la.get(i);
            for (int j = 0; j < lb.size(); j++) {
                String b = lb.get(j);
                getInput().setQuick(i * lb.size() + j, 1d);
                if (invert) {
                    getValues().setQuick(i * lb.size() + j, 1 - distance.score(distance.prepare(a), distance.prepare(b)));
                } else {
                    getValues().setQuick(i * lb.size() + j, distance.score(distance.prepare(a), distance.prepare(b)));
                }

            }
        }
        setInterval(new Interval(0, (int) getInput().size()));
    }


    @Override
    public FunctionInput copy() {
        FuzzyOverlapInputSet nin = new FuzzyOverlapInputSet(la, lb, weightsA, weightsB, distance, false);
        nin.setInput(getInput().copy());
        nin.setValues(getValues().copy());
        if (getPermutation() != null) {
            nin.setPermutation(getPermutation());
        }
        return nin;
    }

}
