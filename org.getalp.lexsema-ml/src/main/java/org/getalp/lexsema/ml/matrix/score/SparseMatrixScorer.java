package org.getalp.lexsema.ml.matrix.score;

import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;

public interface SparseMatrixScorer extends MatrixScorer {
    public double computeScore(SparseDoubleMatrix2D input);
}
