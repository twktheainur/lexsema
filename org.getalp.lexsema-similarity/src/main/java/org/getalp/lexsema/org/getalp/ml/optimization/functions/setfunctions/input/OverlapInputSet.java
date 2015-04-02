package org.getalp.lexsema.org.getalp.ml.optimization.functions.setfunctions.input;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import com.wcohen.ss.AbstractStringDistance;
import org.getalp.ml.optimization.functions.input.FunctionInput;
import org.getalp.ml.optimization.functions.setfunctions.input.AbstractSetFunctionInput;
import org.getalp.ml.optimization.functions.setfunctions.input.Interval;

import java.util.Collections;
import java.util.List;

public class OverlapInputSet extends AbstractSetFunctionInput {

    private final AbstractStringDistance distance;
    private List<String> la;
    private List<String> lb;
    private List<Double> weightsA;
    private List<Double> weightsB;
    private boolean invert;

    public OverlapInputSet(final List<String> la, final List<String> lb, final List<Double> weightsA, final List<Double> weightsB, final AbstractStringDistance distance, boolean compute) {
        this.la = Collections.unmodifiableList(la);
        this.lb = Collections.unmodifiableList(lb);
        if (weightsA != null && weightsB != null) {
            this.weightsA = Collections.unmodifiableList(weightsA);
            this.weightsB = Collections.unmodifiableList(weightsB);
        }
        this.distance = distance;
        if (compute) {
            compute();
        }
    }

    public OverlapInputSet(final List<String> la, final List<String> lb, final List<Double> weightsA, final List<Double> weightsB, final AbstractStringDistance distance) {
        this(la, lb, weightsA, weightsB, distance, true);
    }
    private void compute() {
        setInput(new DenseDoubleMatrix1D(la.size() * lb.size()));
        setValues(new DenseDoubleMatrix1D(la.size() * lb.size()));
        for (int i = 0; i < la.size(); i++) {
            String a = la.get(i);
            for (int j = 0; j < lb.size(); j++) {
                String b = lb.get(j);
                getInput().setQuick(i * lb.size() + j, 1d);
                double value = 0d;
                if (distance != null) {
                    value = distance.score(distance.prepare(a), distance.prepare(b));
                } else if (a.equals(b)) {
                    value = 1;
                }
                if (invert) {
                    value = 1 - value;
                }
                getValues().setQuick(i * lb.size() + j, value);
            }
        }
        setInterval(new Interval(0, (int) getInput().size()));
    }


    @SuppressWarnings({"LawOfDemeter", "LocalVariableOfConcreteClass"})
    @Override
    public FunctionInput copy() {
        OverlapInputSet nin = new OverlapInputSet(la, lb, weightsA, weightsB, distance, false).setInvert(invert);
        nin.setInput(getInput().copy());
        nin.setValues(getValues().copy());
        if (getPermutation() != null) {
            nin.setPermutation(getPermutation());
        }
        return nin;
    }

    @SuppressWarnings({"PublicMethodNotExposedInInterface", "BooleanParameter", "MethodReturnOfConcreteClass"})
    public OverlapInputSet setInvert(boolean invert) {
        this.invert = invert;
        return this;
    }

}
