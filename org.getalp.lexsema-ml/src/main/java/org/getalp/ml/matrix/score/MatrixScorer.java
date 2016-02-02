package org.getalp.ml.matrix.score;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

import java.io.Serializable;

public interface MatrixScorer extends Serializable{
    double computeScore(DoubleMatrix2D input);
}
