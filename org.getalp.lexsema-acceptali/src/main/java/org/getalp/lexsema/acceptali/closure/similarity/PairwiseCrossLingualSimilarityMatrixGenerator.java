package org.getalp.lexsema.acceptali.closure.similarity;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.ml.matrix.filters.Filter;


public interface PairwiseCrossLingualSimilarityMatrixGenerator {
    public DoubleMatrix2D getScoreMatrix();
    public DoubleMatrix2D getScoreMatrix(Filter filter);
    public void generateMatrix();
}
