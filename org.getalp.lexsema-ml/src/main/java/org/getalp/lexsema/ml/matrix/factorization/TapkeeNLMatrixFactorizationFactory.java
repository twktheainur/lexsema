package org.getalp.lexsema.ml.matrix.factorization;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

import java.nio.file.Path;

public class TapkeeNLMatrixFactorizationFactory implements MatrixFactorizationFactory {
    private final Path tapkeePath;
    private final TapkeeNLMatrixFactorization.Method method;
    int k = -1;

    public TapkeeNLMatrixFactorizationFactory(TapkeeNLMatrixFactorization.Method method, Path tapkeePath) {
        this.method = method;
        this.tapkeePath = tapkeePath;
    }

    @Override
    public MatrixFactorization factorize(DoubleMatrix2D A) {

        MatrixFactorization fac;
        if(k>0){
            fac = new TapkeeNLMatrixFactorization(tapkeePath,A, method,k);
        } else {
            fac = new TapkeeNLMatrixFactorization(tapkeePath,A, method);
        }
        fac.compute();
        return fac;
    }

    @Override
    public void setK(int k) {
        this.k = k;
    }
}
