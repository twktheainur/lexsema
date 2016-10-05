/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.getalp.lexsema.ml.matrix.factorization;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

/**
 * Factory for {@link PartialSingularValueDecomposition}s.
 */
@SuppressWarnings("deprecation")
public class FastICAMatrixFactorizationFactory implements
        MatrixFactorizationFactory {
    /**
     * The default desired number of base vectors
     */
    protected static final int DEFAULT_K = -1;
    /**
     * The desired number of base vectors
     */
    protected int k;

    /**
     * Creates the factory that creates factorizations that compute the maximum number of
     * base vectors.
     */
    public FastICAMatrixFactorizationFactory() {
        k = DEFAULT_K;
    }

    public MatrixFactorization factorize(DoubleMatrix2D A) {
        FastICAMatrixFactorization fastICAMatrixFactorization = new FastICAMatrixFactorization(
                A);
        if(k>DEFAULT_K){
            fastICAMatrixFactorization.setNum_components(k);
        }
        fastICAMatrixFactorization.compute();
        return fastICAMatrixFactorization;
    }

    /**
     * Returns the number of base vectors <i>k </i>.
     */
    public int getK() {
        return k;
    }

    /**
     * Sets the number of base vectors <i>k </i>.
     *
     * @param k the number of base vectors
     */
    public void setK(int k) {
        this.k = k;
    }
}
