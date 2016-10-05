package org.getalp.lexsema.wsd.score;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.util.ValueScale;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.ml.optimization.functions.input.FunctionInput;
import org.getalp.lexsema.ml.optimization.functions.setfunctions.input.AbstractSetFunctionInput;


public class ConfigurationPairScoreInput extends AbstractSetFunctionInput {
    private final Configuration configuration1;
    private final Document document1;

    private final Configuration configuration2;
    private final Document document2;

    private final SimilarityMeasure sim;
    private double maxRange = 0;
    private double minRange = 1;


    public ConfigurationPairScoreInput(final Configuration configuration1, final Document document1,
                                       final Configuration configuration2, final Document document2,
                                       final SimilarityMeasure sim) {
        this.configuration1 = configuration1;
        this.document1 = document1;
        this.configuration2 = configuration2;
        this.document2 = document2;
        this.sim = sim;
        compute();
    }

    public ConfigurationPairScoreInput(Configuration configuration1, Document document1,
                                       Configuration configuration2, Document document2,
                                       SimilarityMeasure sim, boolean compute) {
        this.configuration1 = configuration1;
        this.document1 = document1;
        this.configuration2 = configuration2;
        this.document2 = document2;
        this.sim = sim;
        if (compute) {
            compute();
        }
    }

    private void compute() {
        setInput(new DenseDoubleMatrix1D(configuration1.size() * configuration2.size()));
        setValues(new DenseDoubleMatrix1D(configuration1.size() * configuration2.size()));
        int pairindex = 0;
        for (int i = 0; i < configuration1.size(); i++) {
            int assingment1 = configuration1.getAssignment(i);
            Sense sense1 = null;
            if (assingment1 != -1) {
                sense1 = document1.getSenses(i).get(assingment1);
            }
            for (int j = 0; j < configuration2.size(); j++) {
                Sense sense2 = null;
                int assingment2 = configuration2.getAssignment(j);
                if (assingment2 != -1) {
                    sense2 = document2.getSenses(j).get(assingment2);
                }
                double score = 0d;
                if (sense1 != null && sense2 != null) {
                    score = ValueScale.scaleValue(
                            0, 1, 0, 5,
                            sense1.computeSimilarityWith(sim, sense2)
                    );
                }
                getInput().setQuick(pairindex, 1d);
                getValues().setQuick(pairindex, score);
                pairindex++;
            }
        }
    }

    @Override
    public FunctionInput copy() {
        ConfigurationPairScoreInput cpsi = new ConfigurationPairScoreInput(configuration1, document1, configuration2, document2, sim, false);
        cpsi.setInput(getInput().copy());
        cpsi.setValues(getValues().copy());
        if (getPermutation() != null) {
            cpsi.setPermutation(getPermutation());
        }
        return cpsi;
    }

    public void setRange(double min, double max) {
        minRange = min;
        maxRange = max;
    }
}
