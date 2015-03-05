package org.getalp.ml.matrix.score;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

/**
 * Created by tchechem on 24/02/15.
 */
public class SumMatrixScorer implements MatrixScorer {
    @Override
    public double computeScore(DoubleMatrix2D input) {
        return input.zSum();
    }
}
