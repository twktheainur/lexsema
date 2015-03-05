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

package org.getalp.ml.matrix.factorization;

/**
 * Performs an iterative matrix factorization.
 */
public interface IterativeMatrixFactorization extends MatrixFactorization {
    /**
     * Returns approximation error achieved after the last iteration of the
     * algorithm or -1 if the approximation error is not available.
     *
     * @return approximation error or -1
     */
    public abstract double getApproximationError();

    /**
     * Returns the number of iterations the algorithm has completed.
     *
     * @return the number of iterations the algorithm has completed
     */
    public abstract int getIterationsCompleted();
}
