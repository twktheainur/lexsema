package org.getalp.lexsema.acceptali.crosslingual;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

/**
 * Created by tchechem on 05/03/15.
 */
public interface PairwiseCrossLingualSimilarityMatrixGenerator {
    DoubleMatrix2D getScoreMatrix();
}
