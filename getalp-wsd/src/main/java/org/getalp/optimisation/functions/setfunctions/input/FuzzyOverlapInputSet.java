package org.getalp.optimisation.functions.setfunctions.input;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import com.wcohen.ss.AbstractStringDistance;
import org.getalp.optimisation.functions.input.FunctionInput;

import java.util.List;

public class FuzzyOverlapInputSet extends SetFunctionInput {

    private final AbstractStringDistance distance;
    List<String> la;
    List<String> lb;
    List<Double> weightsA;
    List<Double> weightsB;


    public FuzzyOverlapInputSet(List<String> la, List<String> lb, List<Double> weightsA, List<Double> weightsB, AbstractStringDistance distance) {
        this.la = la;
        this.lb = lb;
        this.weightsA = weightsA;
        this.weightsB = weightsB;
        this.distance = distance;
        compute();
    }

    public FuzzyOverlapInputSet(List<String> la, List<String> lb, List<Double> weightsA, List<Double> weightsB, AbstractStringDistance distance, boolean compute) {
        this.la = la;
        this.lb = lb;
        this.weightsA = weightsA;
        this.weightsB = weightsB;
        this.distance = distance;
        if(compute){
            compute();
        }
    }

    private final void compute(){
        setInput(new DenseDoubleMatrix1D(la.size()*lb.size()));
        setValues(new DenseDoubleMatrix1D(la.size()*lb.size()));
        for (int i = 0; i < la.size(); i++) {
            String a = la.get(i);
            double wa = 1d;
            if (weightsA != null) {
                wa = weightsA.get(i);
            }
            for (int j = 0; j < lb.size(); j++) {
                double wb = 1d;
                if (weightsB != null) {
                    wb = weightsB.get(i);
                }
                String b = lb.get(j);
                //getInput().setQuick(i * lb.size() + j, (wa + wb) / 2d);
                getInput().setQuick(i * lb.size() + j, 1d);
                getValues().setQuick(i * lb.size() + j, 1-distance.score(distance.prepare(a), distance.prepare(b)));

            }
        }
        setInterval(new Interval(0,(int)getInput().size()));
    }


    @Override
    public FunctionInput copy() {
        FuzzyOverlapInputSet nin = new FuzzyOverlapInputSet(la,lb,weightsA,weightsB,distance,false);
        nin.setInput(getInput().copy());
        nin.setValues(getValues().copy());
        if(getPermutation()!=null){
            nin.setPermutation(getPermutation());
        }
        return nin;
    }

}
