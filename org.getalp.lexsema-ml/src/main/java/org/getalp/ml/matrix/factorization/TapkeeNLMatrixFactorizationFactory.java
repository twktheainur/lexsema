package org.getalp.ml.matrix.factorization;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

public class TapkeeNLMatrixFactorizationFactory implements MatrixFactorizationFactory {
    TapkeeNLMatrixFactorization.Method method;
    int k = -1;

    public TapkeeNLMatrixFactorizationFactory(TapkeeNLMatrixFactorization.Method method) {
        this.method = method;
    }

    @Override
    public MatrixFactorization factorize(DoubleMatrix2D A) {

        MatrixFactorization fac;
        if(k>0){
            fac = new TapkeeNLMatrixFactorization(A, method,k);
        } else {
            fac = new TapkeeNLMatrixFactorization(A, method);
        }
        fac.compute();
        return fac;
    }

    @Override
    public void setK(int k) {
        this.k = k;
    }
}
