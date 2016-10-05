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
 * A factory of {@link MatrixFactorization}s.
 */
@SuppressWarnings("deprecation")
public interface MatrixFactorizationFactory {
    /**
     * Factorizes matrix <code>A</code>.
     *
     * @param A matrix to be factorized.
     */
    public MatrixFactorization factorize(DoubleMatrix2D A);

    public void setK(int k);
}
