package org.getalp.lexsema.wsd.score;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.ml.optimization.functions.input.FunctionInput;
import org.getalp.lexsema.ml.optimization.functions.setfunctions.input.AbstractSetFunctionInput;
import org.getalp.lexsema.ml.optimization.functions.setfunctions.input.Interval;

import java.util.List;


public class ConfigurationEntryPairwiseScoreInput extends AbstractSetFunctionInput {

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
            List<Sense> senses = document.getSenses(configuration.getStart(), currentIndex);
            if (!senses.isEmpty()) {
                currentWordSelectedSense = senses.get(currentAssignment);
            }
        }
        int pairIndex = 0;
        for (int i = currentIndex; i < configuration.size(); i++) {
            double value = 0;
            if (i != currentIndex) {
                int assig = configuration.getAssignment(i);
                if (assig != -1) {
                    List<Sense> senses = document.getSenses(configuration.getStart(), i);
                    if (!senses.isEmpty()) {
                        Sense otherWordSense = senses
                                .get(assig);

                        getInput().setQuick(pairIndex, 1d);
                        if (currentWordSelectedSense != null && otherWordSense != null) {
                            value = currentWordSelectedSense.computeSimilarityWith(sim, otherWordSense);
                            //System.err.println(value);
                            if(!Double.isNaN(value)) {
                                getValues().setQuick(pairIndex, value);
                            } else {
                                getValues().setQuick(pairIndex, 0);
                            }
                        }
                    }
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
