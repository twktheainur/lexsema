package org.getalp.ml.matrix.factorization;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

public class TapkeeNLMatrixFactorizationFactory implements MatrixFactorizationFactory {
    TapkeeNLMatrixFactorization.Method method;

    public TapkeeNLMatrixFactorizationFactory(TapkeeNLMatrixFactorization.Method method) {
        this.method = method;
    }

    @Override
    public MatrixFactorization factorize(DoubleMatrix2D A) {
        MatrixFactorization fac = new TapkeeNLMatrixFactorization(A, method);
        fac.compute();
        return fac;
    }
}
