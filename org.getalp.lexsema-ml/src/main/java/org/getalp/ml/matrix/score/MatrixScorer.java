package org.getalp.ml.matrix.score;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

public interface MatrixScorer {
    public double computeScore(DoubleMatrix2D input);
}
