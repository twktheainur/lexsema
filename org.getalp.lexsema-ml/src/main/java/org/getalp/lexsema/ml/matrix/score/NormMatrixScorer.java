package org.getalp.lexsema.ml.matrix.score;

import cern.colt.matrix.Norm;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;

public class NormMatrixScorer implements MatrixScorer {

    Norm norm;

    public NormMatrixScorer(Norm norm) {
        this.norm = norm;
    }

    @Override
    public double computeScore(DoubleMatrix2D input) {
        return DenseDoubleAlgebra.DEFAULT.norm(input, norm);
    }
}
