package org.getalp.disambiguation.score;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.io.Document;
import org.getalp.io.Sense;
import org.getalp.optimization.functions.input.FunctionInput;
import org.getalp.optimization.functions.setfunctions.input.Interval;
import org.getalp.optimization.functions.setfunctions.input.SetFunctionInput;
import org.getalp.similarity.semantic.SimilarityMeasure;

public class ConfigurationEntryPairwiseScoreInput extends SetFunctionInput {

    private final SimilarityMeasure sim;
    private Configuration configuration;
    private Document document;
    private int currentIndex;


    public ConfigurationEntryPairwiseScoreInput(Configuration c, Document d, int currentIndex, SimilarityMeasure sim) {
        document = d;
        configuration = c;
        this.sim = sim;
        this.currentIndex = currentIndex;
        compute();
    }

    public ConfigurationEntryPairwiseScoreInput(Configuration c, Document d, int currentIndex, SimilarityMeasure sim, boolean compute) {
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
        ConfigurationEntryPairwiseScoreInput nin = new ConfigurationEntryPairwiseScoreInput(configuration, document, currentIndex, sim, false);
        nin.setInput(getInput().copy());
        nin.setValues(getValues().copy());
        if (getPermutation() != null) {
            nin.setPermutation(getPermutation());
        }
        return nin;
    }

}
