package org.getalp.lexsema.examples.points;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.ml.matrix.filters.Filter;


public interface PairwiseSimilarityMatrixGenerator {
    public DoubleMatrix2D getScoreMatrix();
    public DoubleMatrix2D getScoreMatrix(Filter filter);
    public void generateMatrix();
}
