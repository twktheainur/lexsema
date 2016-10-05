package org.getalp.lexsema.ml.matrix.score.generator;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;

public interface DenseScoreMatrixGenerator {
    public DenseDoubleMatrix2D generateDenseScoreMatrix();
}
