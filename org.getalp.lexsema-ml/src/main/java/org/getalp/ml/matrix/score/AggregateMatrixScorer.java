package org.getalp.ml.matrix.score;

import cern.colt.function.tdouble.DoubleDoubleFunction;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;

public class AggregateMatrixScorer implements MatrixScorer {

    DoubleDoubleFunction aggregationFunction;

    public AggregateMatrixScorer(DoubleDoubleFunction aggregationFunction) {
        this.aggregationFunction = aggregationFunction;
    }

    @Override
    public double computeScore(DoubleMatrix2D input) {
        return input.aggregate(aggregationFunction, DoubleFunctions.identity);
    }
}
