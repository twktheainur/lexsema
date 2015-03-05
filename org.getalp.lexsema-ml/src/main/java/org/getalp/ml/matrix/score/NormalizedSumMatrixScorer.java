package org.getalp.ml.matrix.score;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

/**
 * Created by tchechem on 24/02/15.
 */
public class NormalizedSumMatrixScorer implements MatrixScorer {
    @Override
    public double computeScore(DoubleMatrix2D input) {
        DoubleMatrix2D copy = input.copy();
        copy.normalize();
        return copy.zSum();
    }
}
