package org.getalp.lexsema.examples.points;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.ml.matrix.filters.Filter;


public interface PairwiseSimilarityMatrixGenerator {
    DoubleMatrix2D getScoreMatrix();
    DoubleMatrix2D getScoreMatrix(Filter filter);
    void generateMatrix();
}
