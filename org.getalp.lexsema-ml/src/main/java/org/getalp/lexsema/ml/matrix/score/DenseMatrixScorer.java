package org.getalp.lexsema.ml.matrix.score;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;

public interface DenseMatrixScorer extends MatrixScorer {
    public double computeScore(DenseDoubleMatrix2D input);
}
