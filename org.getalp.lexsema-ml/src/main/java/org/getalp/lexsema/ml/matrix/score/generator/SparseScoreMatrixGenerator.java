package org.getalp.lexsema.ml.matrix.score.generator;

import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;

public interface SparseScoreMatrixGenerator {
    public SparseDoubleMatrix2D generateSparseScoreMatrix();

}
