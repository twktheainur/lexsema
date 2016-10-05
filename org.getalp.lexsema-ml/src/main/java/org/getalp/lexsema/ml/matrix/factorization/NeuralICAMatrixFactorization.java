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


import cern.colt.function.tdouble.DoubleFunction;
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;

/**
 * Performs matrix factorization using the Neural ICA algorithm
 * adapted from the implementation at http://shulgadim.blogspot.fr/2014/02/independent-component-analysis-ica.html
 */
@SuppressWarnings("deprecation")
public class NeuralICAMatrixFactorization extends MatrixFactorizationBase {

    private final double eta = 0.01;
    private DoubleFunction F;
    private DoubleFunction G;
    private DoubleMatrix2D I;

    /**
     * Creates the NNINonnegativeMatrixFactorizationED object for matrix A. Before
     * accessing results, perform computations by calling the {@link #compute()}method.
     *
     * @param A matrix to be factorized
     */
    public NeuralICAMatrixFactorization(DoubleMatrix2D A) {
        super(A);
        F = new FFunction();
        G = new GFunction();
    }

    @Override
    public void compute() {
        U = new DenseDoubleMatrix2D(A.rows(), A.columns());
        V = DoubleFactory2D.dense.identity(A.rows());
        I = DoubleFactory2D.dense.identity(A.rows());
        for (int row = 0; row < A.rows(); row++) {
            DoubleMatrix1D y = U.viewRow(row);
            V.zMult(A.viewRow(row), y);
            DoubleMatrix2D dW = DenseDoubleAlgebra.DEFAULT.multOuter(y.assign(F), y.assign(G), null);
            dW.assign(I, DoubleFunctions.minus).assign(DoubleFunctions.mult(eta));
            V.assign(V.zMult(dW, null), DoubleFunctions.plus);
        }
    }

    public String toString() {
        return "NeuralICA";
    }

    private class FFunction implements DoubleFunction {
        @Override
        public double apply(double argument) {
            return argument;
        }
    }

    private class GFunction implements DoubleFunction {
        @Override
        public double apply(double argument) {
            return Math.tanh(10 * argument);
        }
    }
}
