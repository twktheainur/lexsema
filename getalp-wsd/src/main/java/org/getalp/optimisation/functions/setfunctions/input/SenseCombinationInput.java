package org.getalp.optimisation.functions.setfunctions.input;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.Sense;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.optimisation.functions.input.FunctionInput;
import org.getalp.similarity.local.SimilarityMeasure;

public class SenseCombinationInput extends SetFunctionInput {

    private final SimilarityMeasure sim;
    private Configuration configuration;
    private Document document;
    private int currentIndex;


    public SenseCombinationInput(Configuration c, Document d, int currentIndex, SimilarityMeasure sim) {
        document = d;
        configuration = c;
        this.sim = sim;
        this.currentIndex = currentIndex;
        compute();
    }

    public SenseCombinationInput(Configuration c, Document d, int currentIndex, SimilarityMeasure sim, boolean compute) {
        document = d;
        configuration = c;
        this.sim = sim;
        this.currentIndex = currentIndex;
        if (compute) {
            compute();
        }
    }

    private void compute() {
        setInput(new DenseDoubleMatrix1D(configuration.size() - 1));
        setValues(new DenseDoubleMatrix1D(configuration.size() - 1));
        int currentAssignment = configuration.getAssignment(currentIndex);
        Sense currentWordSelectedSense = null;
        if (currentAssignment != -1) {
            currentWordSelectedSense = document.getSenses(configuration, currentIndex).get(currentAssignment);
        }
        int pairIndex = 0;
        for (int i = 0; i < configuration.size(); i++) {
            double value = 0;
            if (i != currentIndex) {
                int assig = configuration.getAssignment(i);
                if (assig != -1) {
                    Sense otherWordSense = document.getSenses(configuration, i)
                            .get(assig);
                    getInput().setQuick(pairIndex, 1d);
                    if (currentWordSelectedSense != null) {
                        value = sim.compute(currentWordSelectedSense, otherWordSense);
                    }
                    getValues().setQuick(pairIndex, value);
                    pairIndex++;
                }
            }
        }
        setInterval(new Interval(0, (int) getInput().size()));
    }


    @Override
    public FunctionInput copy() {
        SenseCombinationInput nin = new SenseCombinationInput(configuration, document, currentIndex, sim, false);
        nin.setInput(getInput().copy());
        nin.setValues(getValues().copy());
        if (getPermutation() != null) {
            nin.setPermutation(getPermutation());
        }
        return nin;
    }

}
