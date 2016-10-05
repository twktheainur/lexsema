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


import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.algo.decomposition.DenseDoubleEigenvalueDecomposition;
import cern.colt.matrix.tdouble.algo.decomposition.DenseDoubleSingularValueDecomposition;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;
import org.getalp.lexsema.ml.matrix.factorization.fastica.NegativeEntropyEstimator;
import org.getalp.lexsema.ml.matrix.factorization.fastica.LogCosh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Performs matrix factorization using the Non-negative Matrix Factorization algorithm
 * with minimization of Euclidean Distance between A and UV' and multiplicative updating.
 */
@SuppressWarnings("deprecation")
public class FastICAMatrixFactorization extends MatrixFactorizationBase {


    private static final DenseDoubleAlgebra algebra = DenseDoubleAlgebra.DEFAULT;
    /**
     * number of rows (instances) in X
     */
    private final int m;
    /**
     * number of columns (features) in X
     */
    private final int n;
    Logger logger = LoggerFactory.getLogger(FastICAMatrixFactorization.class);
    // Convergence tolerance
    private double tolerance;
    // Iteration limit
    private int max_iter;
    // Whiten the data if true
    private boolean whiten;
    // The estimated unmixing matrix
    private DoubleMatrix2D W;
    // The pre-whitening matrix
    private DoubleMatrix2D K;
    // the product of K and W
    private DoubleMatrix2D KW;
    // The mean value of each column of the input matrix
    private DoubleMatrix1D X_means;
    // Reference to non-linear neg-entropy estimator function
    private final NegativeEntropyEstimator G;
    // Number of components to output
    private int num_components;

    /**
     * General FastICA instance constructor with an arbitrary (user-supplied)
     * function to estimate negative entropy. This implementation does not
     * perform automatic component selection or reduction.
     */
    public FastICAMatrixFactorization(DoubleMatrix2D A) {
        super(A);
        G = new LogCosh();
        num_components = A.columns();
        m = A.rows();
        n = A.columns();
        whiten = true;
        tolerance = 0.5;
        max_iter = 100;
    }

    /*
     * Perform symmetric decorrelation on the input matrix to ensure that each
     * column is independent from all the others. This is required in order
     * to prevent FastICA from solving for the same components in multiple
     * columns.
     *
     * NOTE: There are only real eigenvalues for the W matrix
     *
     * W <- W * (W.T * W)^{-1/2}
     *
     * Python (Numpy):
     *   s, u = linalg.eigh(np.dot(W.T, W))
     *   W = np.dot(W, np.dot(u * (1. / np.sqrt(s)), u))
     * Matlab:
     *   B = B * real(inv(B' * B)^(1/2))
     *
     */
    @SuppressWarnings("rawtypes")
    private static DoubleMatrix2D symmetricDecorrelation(DoubleMatrix2D x) {

        double d;
        DoubleMatrix2D QL;
        DoubleMatrix2D Q;

        DenseDoubleEigenvalueDecomposition EVD =
                new DenseDoubleEigenvalueDecomposition(algebra.transpose(x).zMult(x, null));
        DoubleMatrix1D eigenValues = EVD.getRealEigenvalues();
        int len = (int) eigenValues.size();
        QL = new DenseDoubleMatrix2D(len, len);
        Q = EVD.getV();

        // Scale each column of the eigenvector matrix by the square root of
        // the reciprocal of the associated eigenvalue
        for (int i = 0; i < len; i++) {
            d = eigenValues.getQuick(i);
            d = (d + Math.abs(d)) / 2;  // improve numerical stability by eliminating small negatives near singular matrix zeros
            DoubleMatrix1D QROW = Q.viewRow(i).copy();
            DoubleMatrix1D QROWSQRT = QROW;
            if(d>0) {
                QROWSQRT = QROW.assign(DoubleFunctions.div(Math.sqrt(d)));
            }
            QL.viewRow(i).assign(QROWSQRT);
        }
        DoubleMatrix2D QT = algebra.transpose(Q);
        DoubleMatrix2D QLMQT = QL.zMult(QT, null);
        return x.zMult(QLMQT, null);
    }

    /*
     * Randomly generate a square matrix drawn from a standard gaussian
     * distribution.
     */
    private static DoubleMatrix2D gaussianSquareMatrix(int size) {
        DoubleMatrix2D ret = new DenseDoubleMatrix2D(size, size);
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ret.set(i, j, rand.nextGaussian());
            }
        }
        return ret;
    }


    /**
     * Mixing matrix
     *
     * @return Mixing matrix
     */
    private DoubleMatrix2D getEM() {
        return algebra.inverse(K.zMult(W, null));
    }

    /**
     * Project a row-indexed matrix of data into the ICA domain by applying
     * the pre-whitening and un-mixing matrices. This method should not be
     * called prior to running fit() with input data.
     *
     * @param data rectangular double[][] array containing values; the
     *             number of columns should match the data provided to the
     *             fit() method for training
     * @return result    rectangular double[][] array containing the projected
     * output values
     */
    private DoubleMatrix2D transform(DoubleMatrix2D data) {
        for (int column = 0; column < data.columns(); column++) {
            data.viewColumn(column).assign(DoubleFunctions.minus(X_means.get(column)));
        }
        return data.zMult(KW, null);
    }

    /**
     * Estimate the unmixing matrix for the data provided
     */
    private void fit() throws Exception {

        // mean center the attributes in X
        double[] means = center(A);
        X_means = new DenseDoubleMatrix1D(means);

        // get the size parameter of the symmetric W matrix; size cannot be
        // larger than the number of samples or the number of features
        num_components = Math.min(Math.min(m, n), num_components);

        K = DoubleFactory2D.dense.identity(num_components);  // init K
        if (whiten) {
            A = whiten(A);  // sets K
        }

        // start with an orthogonal initial W matrix drawn from a standard Normal distribution
        W = symmetricDecorrelation(gaussianSquareMatrix(num_components));

        // fit the data
        parallel_ica();  // solves for W

        // Store the resulting transformation matrix
        K.zMult(W, KW);

    }

    /*
     * FastICA main loop - using default symmetric decorrelation. (i.e.,
     * estimate all the independent components in parallel)
     */
    private void parallel_ica() throws Exception {

        double tmp;
        double lim;
        DoubleMatrix2D W_next;
        DoubleMatrix1D newRow = new DenseDoubleMatrix1D(A.rows());
        DoubleMatrix1D oldRow = new DenseDoubleMatrix1D(A.rows());


        for (int iter = 0; iter < max_iter; iter++) {

            // Estimate the negative entropy and first derivative average
            G.estimate(A.zMult(W, null));

            // Update the W matrix
            W_next = updateW(newRow, oldRow);

            // Test convergence criteria for W
            lim = 0;
            for (int i = 0; i < W.rows(); i++) {
                newRow = W_next.viewRow(i);
                oldRow = W.viewRow(i);
                tmp = newRow.zDotProduct(oldRow);
                tmp = Math.abs(Math.abs(tmp) - 1);
                if (tmp > lim) {
                    lim = tmp;
                }
            }
            W = W_next;

            if (lim < tolerance) {
                return;
            }
        }

        throw new Exception("ICA did not converge - try again with more iterations.");
    }

    private DoubleMatrix2D updateW(DoubleMatrix1D oldRow, DoubleMatrix1D newRow) {
        DoubleMatrix2D W_next;
        final DoubleMatrix2D transpose = algebra.transpose(A);
        final DoubleMatrix2D doubleMatrix2D = transpose.zMult(G.getGx(), null);
        W_next = doubleMatrix2D.assign(DoubleFunctions.mult(1d / n));
        for (int i = 0; i < num_components; i++) {
            newRow.assign(W_next.viewRow(i));
            oldRow.assign(W.viewRow(i));
            W_next.viewRow(i).assign(newRow.assign(oldRow.assign(G.getG_x()), DoubleFunctions.minus));
        }
        W_next = symmetricDecorrelation(W_next);
        return W_next;
    }

    /*
     * Whiten a matrix of column vectors by decorrelating and scaling the
     * elements according to: x_new = ED^{-1/2}E'x , where E is the
     * orthogonal matrix of eigenvectors of E{xx'}. In this implementation
     * (based on the FastICA sklearn Python package) the eigen decomposition is
     * replaced with the SVD.
     *
     * The decomposition is ambiguous with regard to the direction of
     * column vectors (they can be either +/- without changing the result).
     */
    @SuppressWarnings("rawtypes")
    private DoubleMatrix2D whiten(DoubleMatrix2D x) {
        // get compact SVD (D matrix is min(m,n) square)
        DenseDoubleSingularValueDecomposition svd = algebra.svd(x);

        // K should only keep `num_components` columns if performing
        // dimensionality reduction
        final DoubleMatrix2D v = svd.getV();
        final DoubleMatrix2D doubleMatrix2D = v
                .zMult(algebra
                        .inverse(svd.getS()), K);
        K = doubleMatrix2D
                .viewPart(0, 0, doubleMatrix2D.rows(), num_components);
//		K = K.scale(-1);  // sklearn returns this version for K; doesn't affect results

//		return x.mult(K).scale(Math.sqrt(m));  // sklearn scales the input
        return x.zMult(K, null);
    }

    /*
     * Center the input matrix and store it in X by subtracting the average of
     * each column vector from every element in the column
     */
    private double[] center(DoubleMatrix2D x) {
        DoubleMatrix1D col;
        int rows = x.rows();
        int columns = x.columns();
        double[] means;

        means = new double[columns];
        for (int i = 0; i < columns; i++) {
            col = x.viewColumn(i);
            means[i] = col.zSum() / rows;
            for (int j = 0; j < rows; j++) {
                col.set(j, col.get(j) - means[i]);
            }
            A.viewColumn(i).assign(col);
        }
        return means;
    }


    @Override
    public void compute() {
        try {
            fit();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        V = transform(A);
    }

    public String toString() {
        return "FastICA";
    }

    @SuppressWarnings({"MethodReturnOfConcreteClass", "PublicMethodNotExposedInInterface"})
    public FastICAMatrixFactorization setTolerance(double tolerance) {
        this.tolerance = tolerance;
        return this;
    }

    @SuppressWarnings({"MethodReturnOfConcreteClass", "PublicMethodNotExposedInInterface"})
    public FastICAMatrixFactorization setMax_iter(int max_iter) {
        this.max_iter = max_iter;
        return this;
    }

    @SuppressWarnings({"MethodReturnOfConcreteClass", "PublicMethodNotExposedInInterface", "BooleanParameter"})
    public FastICAMatrixFactorization setWhiten(boolean whiten) {
        this.whiten = whiten;
        return this;
    }

    @SuppressWarnings({"MethodReturnOfConcreteClass", "PublicMethodNotExposedInInterface"})
    public FastICAMatrixFactorization setNum_components(int num_components) {
        this.num_components = num_components;
        return this;
    }
}
