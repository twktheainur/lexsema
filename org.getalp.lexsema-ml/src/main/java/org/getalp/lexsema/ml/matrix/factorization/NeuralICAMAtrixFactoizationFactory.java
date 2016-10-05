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
public class NeuralICAMAtrixFactoizationFactory implements
        MatrixFactorizationFactory {
    /**
     * The desired number of base vectors
     */
    protected int k;

    /**
     * The default desired number of base vectors
     */
    /**
     * Creates the factory that creates factorizations that compute the maximum number of
     * base vectors.
     */
    public NeuralICAMAtrixFactoizationFactory() {
    }

    public MatrixFactorization factorize(DoubleMatrix2D A) {
        NeuralICAMatrixFactorization neuralICAMatrixFactorization = new NeuralICAMatrixFactorization(
                A);

        neuralICAMatrixFactorization.compute();

        return neuralICAMatrixFactorization;
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
