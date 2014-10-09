package org.getalp.optimisation.functions.setfunctions.submodular;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.Sense;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.optimisation.functions.cache.SetFunctionCache;
import org.getalp.optimisation.functions.input.FunctionInput;
import org.getalp.optimisation.functions.setfunctions.input.SetFunctionInput;
import org.getalp.similarity.local.SimilarityMeasure;


public class PairwiseSimilarity extends SetFunctionInput {
    private Document document;
    private DoubleMatrix1D values;
    private DoubleMatrix1D inputVector;
    private int indexOffset;
    SetFunctionCache cache;
    SimilarityMeasure sim;

    protected PairwiseSimilarity(SimilarityMeasure sim, Document d, Configuration c, SetFunctionCache oracleCache, int w, int start, int end) {
        document = d;
        cache = oracleCache;
        this.sim = sim;
        inputVector = new DenseDoubleMatrix1D(end-start);

        Sense a = document.getSense().get(indexOffset).get(c.getAssignment(w));

        indexOffset = start;

        for (int pairIndex=0;pairIndex<end && pairIndex<document.getWords().size();pairIndex++) {
            Sense b = document.getSense().get(pairIndex).get(c.getAssignment(pairIndex));
            inputVector.setQuick(pairIndex,sim.compute(a,b));
        }

    }

    @Override
    public DoubleMatrix1D getInput() {
        return inputVector;
    }

    @Override
    public FunctionInput copy() {
        return null;
    }

    @Override
    public DoubleMatrix1D getValues() {
        return null;
    }

    @Override
    public DoubleMatrix1D getPermutation() {
        return null;
    }
}
