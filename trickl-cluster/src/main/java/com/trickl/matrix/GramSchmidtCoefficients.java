/*
 * This file is part of the Trickl Open Source Libraries.
 *
 * Trickl Open Source Libraries - http://open.trickl.com/
 *
 * Copyright (C) 2011 Tim Gee.
 *
 * Trickl Open Source Libraries are free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Trickl Open Source Libraries are distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.trickl.matrix;


import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;

// Gram-Schmidt process for orthnormalizing a set of vectors
// See http://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process
// Only calculates the projection coefficients and the norms of the orthogonal residuals,
// avoids calculation of the orthogonal basis itself. (Note, this can still be obtained
// through getS()).
// As such, the calculation complexity
// is only proportional to the density of the input.
// orthornormalBasis(n) = (A(n) - Sum(i=0, i=n-1){coeff(i) * A(i)} / orthogonalNorm(i).
// V is an optional set of orthogonal basis that A should also be orthogonal to.
// * Note the point of this procedure is that V can have a high density, implying also that
// the output orthogonal basis have a high density, but the algorithm is only dependent on the
// lower input density.
// T is an optional transformation for the supplied matrix V, such that T * V is the existing
// orthogonal basis.
public class GramSchmidtCoefficients {

    private static final double tolerance = 1e-13;
    DenseDoubleAlgebra algebra = DenseDoubleAlgebra.DEFAULT;
    private DoubleMatrix2D A;
    private DoubleMatrix2D V;
    private DoubleMatrix2D T;
    private DoubleMatrix2D aCoefficients;
    private DoubleMatrix2D vCoefficients;
    private int sRank;
    private int sMaxRank;

    public GramSchmidtCoefficients(DoubleMatrix2D A) {
        this(A, A.like(0, A.columns()));
    }

    public GramSchmidtCoefficients(DoubleMatrix2D A, DoubleMatrix2D V) {
        this(A, V, DoubleFactory2D.dense.identity(V.rows()));
    }

    public GramSchmidtCoefficients(DoubleMatrix2D A, DoubleMatrix2D V, DoubleMatrix2D T) {
        this(A, V, T, Integer.MAX_VALUE);
    }

    public GramSchmidtCoefficients(DoubleMatrix2D A, DoubleMatrix2D V, DoubleMatrix2D T, int maxRank) {
        this.A = A;
        this.V = V;
        this.T = T;
        aCoefficients = new DenseDoubleMatrix2D(A.rows(), A.rows());
        vCoefficients = new DenseDoubleMatrix2D(A.rows(), V.rows());
        sMaxRank = maxRank;
        orthonormalize();
    }

    protected void orthonormalize() {
        DoubleMatrix2D sCoefficients = new DenseDoubleMatrix2D(A.rows(), A.rows());
        DoubleMatrix2D vOrthoCoefficients = new DenseDoubleMatrix2D(A.rows(), V.rows());
        DoubleMatrix1D orthogonalNorms = new DenseDoubleMatrix1D(A.rows());

        // Optimized for compressed sparse row matrices
        CompressedSparseRow csr = new CompressedSparseRowAdapter(A);
        int[] aColIndices = csr.getColumnIndices();
        int[] aRowPointers = csr.getRowPointers();
        double[] aData = csr.getData();

        for (int aRow = 0; aRow < A.rows(); ++aRow) {

            // First calculate V coefficients
            for (int vRow = 0; vRow < V.rows(); ++vRow) {
                double vOrthoCoeff = 0;
                for (int k = aRowPointers[aRow]; k < aRowPointers[aRow + 1]; ++k) {
                    int aCol = aColIndices[k];
                    for (int t = 0; t < T.columns(); ++t) {
                        vOrthoCoeff += aData[k] * T.getQuick(vRow, t) * V.getQuick(t, aCol);
                    }
                }

                vOrthoCoefficients.setQuick(aRow, vRow, vOrthoCoeff);
            }

            // Next calculate the residual coefficients
            for (int sRow = 0; sRow < aRow; ++sRow) {
                double aOrthoCoeff = 0;
                double orthogonalNorm = orthogonalNorms.getQuick(sRow);

                for (int k = aRowPointers[aRow]; k < aRowPointers[aRow + 1]; ++k) {
                    int aCol = aColIndices[k];
                    double sOrthogonalCol = 0;
                    for (int aCoeffCol = 0; aCoeffCol < aCoefficients.columns(); ++aCoeffCol) {
                        sOrthogonalCol += aCoefficients.getQuick(sRow, aCoeffCol) * A.getQuick(aCoeffCol, aCol);
                    }

                    // Also make orthogonal to V
                    for (int vCoeffCol = 0; vCoeffCol < V.rows(); ++vCoeffCol) {
                        for (int t = 0; t < T.columns(); ++t) {
                            sOrthogonalCol += vCoefficients.getQuick(sRow, vCoeffCol) * T.getQuick(vCoeffCol, t) * V.getQuick(t, aCol);
                        }
                    }

                    if (orthogonalNorm > 0) {
                        aOrthoCoeff += aData[k] * sOrthogonalCol;
                    }
                }

                sCoefficients.setQuick(aRow, sRow, aOrthoCoeff);
            }

            // Calculate the size of the orthonormals
            double aNorm2 = 0;
            double aFactorsDotProduct = 0;
            for (int k = aRowPointers[aRow]; k < aRowPointers[aRow + 1]; ++k) {
                aNorm2 += aData[k] * aData[k];

                int aCol = aColIndices[k];
                for (int sRow = 0; sRow < aRow; ++sRow) {
                    double orthogonalNorm = orthogonalNorms.getQuick(sRow);
                    if (orthogonalNorm > 0) {
                        double aOrthoColValue = 0;
                        for (int aCoeffCol = 0; aCoeffCol < aCoefficients.columns(); ++aCoeffCol) {
                            aOrthoColValue += aCoefficients.getQuick(sRow, aCoeffCol) * A.getQuick(aCoeffCol, aCol);
                        }
                        for (int vCoeffCol = 0; vCoeffCol < V.rows(); ++vCoeffCol) {
                            for (int t = 0; t < T.columns(); ++t) {
                                aOrthoColValue += vCoefficients.getQuick(sRow, vCoeffCol) * T.getQuick(vCoeffCol, t) * V.getQuick(t, aCol);
                            }
                        }

                        aFactorsDotProduct += aData[k] * sCoefficients.getQuick(aRow, sRow) * aOrthoColValue;
                    }
                }

                // Add in V coefficients
                for (int vRow = 0; vRow < V.rows(); ++vRow) {
                    for (int t = 0; t < T.columns(); ++t) {
                        aFactorsDotProduct += aData[k] * vOrthoCoefficients.getQuick(aRow, vRow) * T.getQuick(vRow, t) * V.getQuick(t, aCol);
                    }
                }
            }

            double factorsNorm2 = 0;
            for (int aOrthoCol = 0; aOrthoCol < aRow; ++aOrthoCol) {
                factorsNorm2 += sCoefficients.getQuick(aRow, aOrthoCol) * sCoefficients.getQuick(aRow, aOrthoCol);
            }
            for (int vOrthoCol = 0; vOrthoCol < vOrthoCoefficients.columns(); ++vOrthoCol) {
                factorsNorm2 += vOrthoCoefficients.getQuick(aRow, vOrthoCol) * vOrthoCoefficients.getQuick(aRow, vOrthoCol);
            }

            double orthonormalNorm2 = Math.max(0, aNorm2 + factorsNorm2 - 2 * aFactorsDotProduct);
            double orthogonalNorm = Math.sqrt(orthonormalNorm2);

            if (orthonormalNorm2 > tolerance) {
                orthogonalNorms.setQuick(aRow, orthogonalNorm);

                // Calculate the coefficents of A and V required to create the orthogonal basis
                for (int sRow = 0; sRow < aRow; ++sRow) {
                    double aOrthoCoeff = sCoefficients.getQuick(aRow, sRow);
                    for (int aCoeffCol = 0; aCoeffCol < aCoefficients.columns(); ++aCoeffCol) {
                        aCoefficients.setQuick(sRank, aCoeffCol, aCoefficients.getQuick(sRank, aCoeffCol)
                                - aOrthoCoeff * aCoefficients.getQuick(sRow, aCoeffCol) / orthogonalNorm);
                    }

                    for (int vCoeffCol = 0; vCoeffCol < V.rows(); ++vCoeffCol) {
                        vCoefficients.setQuick(sRank, vCoeffCol, vCoefficients.getQuick(sRank, vCoeffCol)
                                - aOrthoCoeff * vCoefficients.getQuick(sRow, vCoeffCol) / orthogonalNorm);
                    }
                }
                for (int vCoeffCol = 0; vCoeffCol < V.rows(); ++vCoeffCol) {
                    vCoefficients.setQuick(sRank, vCoeffCol, vCoefficients.getQuick(sRank, vCoeffCol)
                            - vOrthoCoefficients.getQuick(aRow, vCoeffCol) / orthogonalNorm);
                }
                aCoefficients.setQuick(sRank, aRow, 1 / orthogonalNorm);

                sRank++;
            }
        }

        // Consolidate the output if necessary
        if (sRank < A.rows() || sRank > sMaxRank) {
            // Consolidate the output
            int sNewRank = Math.min(sRank, sMaxRank);
            final DoubleMatrix2D aCoefficientsCopy = aCoefficients.like(sNewRank, aCoefficients.columns());
            final DoubleMatrix2D vCoefficientsCopy = vCoefficients.like(sNewRank, vCoefficients.columns());

            for (int aRow = 0, hRow = 0; aRow < sRank; ++aRow) {
                if (orthogonalNorms.get(aRow) > tolerance) {
                    double norm2 = hRow < sMaxRank - 1 ? 1 : sRank - sMaxRank + 1;
                    aCoefficientsCopy.viewRow(hRow).assign(aCoefficients.viewRow(aRow), DoubleFunctions.plusMultSecond(1 / Math.sqrt(norm2)));
                    vCoefficientsCopy.viewRow(hRow).assign(vCoefficients.viewRow(aRow), DoubleFunctions.plusMultSecond(1 / Math.sqrt(norm2)));
                    hRow = Math.min(hRow + 1, sMaxRank - 1);
                }
            }

            aCoefficients = aCoefficientsCopy;
            vCoefficients = vCoefficientsCopy;

            sRank = sNewRank;
        }
    }

    public DoubleMatrix2D getACoefficients() {
        return aCoefficients;
    }

    public DoubleMatrix2D getVCoefficients() {
        return vCoefficients;
    }

    public int getRank() {
        return sRank;
    }

    /*
     * Get the orthonormal basis.
     * Note, you wouldn't normally call this function, as it defeats
     * the purpose of the class, which is to avoid this explicit calculation
     * where necessary.
     * However, the function is provided for comparison with the standard MGS
     * procedure and for ease of testing.
     */
    public DoubleMatrix2D getS() {
        DoubleMatrix2D S = A.like(sRank, A.columns());
        CompressedSparseRow aCsr = new CompressedSparseRowAdapter(A);
        int[] aColIndices = aCsr.getColumnIndices();
        int[] aRowPointers = aCsr.getRowPointers();
        double[] aData = aCsr.getData();

        DoubleMatrix2D TV = V.like();
        SparseUtils.zMult(V, T, TV.viewDice(), true, true);

        CompressedSparseRow tvCsr = new CompressedSparseRowAdapter(TV);
        int[] tvColIndices = tvCsr.getColumnIndices();
        int[] tvRowPointers = tvCsr.getRowPointers();
        double[] tvData = tvCsr.getData();

        for (int hRow = 0; hRow < sRank; ++hRow) {
            for (int aCoeffCol = 0; aCoeffCol < aCoefficients.columns(); ++aCoeffCol) {
                double coefficient = aCoefficients.get(hRow, aCoeffCol);
                for (int k = aRowPointers[aCoeffCol]; k < aRowPointers[aCoeffCol + 1]; ++k) {
                    double value = aData[k];
                    int aCol = aColIndices[k];
                    S.setQuick(hRow, aCol, S.getQuick(hRow, aCol) + coefficient * value);
                }
            }

            for (int vCoeffCol = 0; vCoeffCol < vCoefficients.columns(); ++vCoeffCol) {
                double coefficient = vCoefficients.get(hRow, vCoeffCol);
                for (int k = tvRowPointers[vCoeffCol]; k < tvRowPointers[vCoeffCol + 1]; ++k) {
                    double value = tvData[k];
                    int aCol = tvColIndices[k];
                    S.setQuick(hRow, aCol, S.getQuick(hRow, aCol) + coefficient * value);
                }
            }
        }

        return S;
    }
}
