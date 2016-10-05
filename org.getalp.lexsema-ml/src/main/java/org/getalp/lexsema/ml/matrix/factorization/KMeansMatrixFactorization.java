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
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;
import org.getalp.lexsema.ml.matrix.Matrices;

/**
 * Performs matrix factorization using the K-means clustering algorithm. This kind of
 * factorization is sometimes referred to as Concept Decomposition Factorization.
 */
@SuppressWarnings("deprecation")
public class KMeansMatrixFactorization extends IterativeMatrixFactorizationBase {
    /**
     * Creates the KMeansMatrixFactorization object for matrix A. Before accessing
     * results, perform computations by calling the {@link #compute()} method.
     *
     * @param A matrix to be factorized. The matrix must have Euclidean length-normalized
     *          columns.
     */
    public KMeansMatrixFactorization(DoubleMatrix2D A) {
        super(A);
    }

    @Override
    public void compute() {
        int n = A.columns();

        // Distances to centroids
        DoubleMatrix2D D = new DenseDoubleMatrix2D(k, n);

        // Object-cluster assignments
        V = new DenseDoubleMatrix2D(n, k);

        // Initialize the centroids with some document vectors
        U = new DenseDoubleMatrix2D(A.rows(), k);
        U.assign(A.viewPart(0, 0, A.rows(), k));

        int[] minIndices = new int[D.columns()];
        double[] minValues = new double[D.columns()];

        for (int iterationsCompletedCurrent = 0; iterationsCompletedCurrent < maxIterations; iterationsCompletedCurrent++) {
            // Calculate cosine distances
            U.zMult(A, D, 1, 0, true, false);

            V.assign(0);
            U.assign(0);

            // For each object
            Matrices.maxInColumns(D, minIndices, minValues);
            for (int i = 0; i < minIndices.length; i++) {
                V.setQuick(i, minIndices[i], 1);
            }

            // Update centroids
            for (int column = 0; column < V.columns(); column++) {
                // Sum
                int count = 0;
                for (int row = 0; row < V.rows(); row++) {
                    if (V.getQuick(row, column) != 0) {
                        count++;
                        U.viewColumn(column).assign(A.viewColumn(row), DoubleFunctions.plus);
                    }
                }

                // Divide
                U.viewColumn(column).assign(DoubleFunctions.div(count));
                Matrices.normalizeColumnL2(U, null);
            }

        }
    }

    public String toString() {
        return "KMMF";
    }
}
