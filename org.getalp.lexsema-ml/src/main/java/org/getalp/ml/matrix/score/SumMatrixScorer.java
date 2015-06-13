package org.getalp.ml.matrix.score;

import cern.colt.matrix.tdouble.DoubleMatrix2D;


public class SumMatrixScorer implements MatrixScorer {
    @Override
    public double computeScore(DoubleMatrix2D input) {
        return input.zSum();
    }
}
