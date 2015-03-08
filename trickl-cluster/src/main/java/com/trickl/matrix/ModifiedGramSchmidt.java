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


import cern.colt.function.tdouble.IntDoubleProcedure;
import cern.colt.function.tdouble.IntIntDoubleFunction;
import cern.colt.map.tdouble.OpenIntDoubleHashMap;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// Gram-Schmidt process for orthnormalizing a set of vectors
// See http://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process
public class ModifiedGramSchmidt {

    private static final double tolerance = 1e-12;
    DenseDoubleAlgebra algebra = DenseDoubleAlgebra.DEFAULT;
    private DoubleMatrix2D A;
    private DoubleMatrix2D S;
    private int sRank;
    private int sMaxRank;

    public ModifiedGramSchmidt(DoubleMatrix2D A) {
        this(A, Integer.MAX_VALUE);
    }

    public ModifiedGramSchmidt(DoubleMatrix2D A, int sMaxRank) {
        this.A = A;
        S = A.like(A.rows(), A.columns());
        this.sMaxRank = sMaxRank;
        orthonormalize();
    }

    protected void orthonormalize() {
        if (A instanceof SparseDoubleMatrix2D) {
            // Optimized for compressed sparse row matrices
            CompressedSparseRow csr = new CompressedSparseRowAdapter(A);
            int[] aColIndices = csr.getColumnIndices();
            int[] aRowPointers = csr.getRowPointers();
            double[] aData = csr.getData();
            int[] sRowPointers = new int[S.rows() + 1];
            List<Integer> sColIndices = new LinkedList<Integer>();

            for (int arow = 0; arow < A.rows(); ++arow) {
                final OpenIntDoubleHashMap residual = new OpenIntDoubleHashMap(A.columns());
                for (int k = aRowPointers[arow]; k < aRowPointers[arow + 1]; ++k) {
                    int column = aColIndices[k];
                    residual.put(column, aData[k]);
                }

                for (int srow = 0; srow < sRank; ++srow) {
                    final double[] coefficient = new double[1];
                    final int srowf = srow;
                    residual.forEachPair(new IntDoubleProcedure() {

                        @Override
                        public boolean apply(int first, double value) {
                            coefficient[0] += value * S.getQuick(srowf, first);
                            return true;
                        }
                    });

                    for (int k = sRowPointers[srow]; k < sRowPointers[srow + 1]; ++k) {
                        int scol = sColIndices.get(k);
                        residual.put(scol, residual.get(scol) - coefficient[0] * S.getQuick(srow, scol));
                    }
                }

                if (!residual.isEmpty()) {
                    final double[] residualNorm = new double[1];
                    residual.forEachPair(new IntDoubleProcedure() {

                        @Override
                        public boolean apply(int first, double value) {
                            residualNorm[0] += value * value;
                            return true;
                        }
                    });
                    residualNorm[0] = Math.sqrt(residualNorm[0]);
                    final int[] sRowColIndices = new int[residual.size()];
                    final int[] sColIndex = new int[1];
                    residual.forEachPair(new IntDoubleProcedure() {

                        @Override
                        public boolean apply(int first, double value) {
                            S.setQuick(sRank, first, value / residualNorm[0]);
                            sRowColIndices[sColIndex[0]++] = first;
                            return true;
                        }
                    });

                    Arrays.sort(sRowColIndices);
                    for (int k = 0; k < sRowColIndices.length; k++) {
                        sColIndices.add(sRowColIndices[k]);
                    }
                    sRowPointers[sRank + 1] = sRowPointers[sRank] + residual.size();
                    sRank++;
                }
            }
        } else {
            for (int arow = 0; arow < A.rows(); ++arow) {
                // For dense matrices
                DoubleMatrix1D residual = A.viewRow(arow);
                for (int srow = 0; srow < sRank; ++srow) {
                    double coefficient = residual.zDotProduct(S.viewRow(srow));
                    residual.assign(S.viewRow(srow), DoubleFunctions.plusMultSecond(-coefficient));
                }

                double residualNorm = Math.sqrt(algebra.norm2(residual));
                if (residualNorm > tolerance) {
                    S.viewRow(sRank).assign(0);
                    S.viewRow(sRank).assign(residual, DoubleFunctions.plusMultSecond(1. / residualNorm));
                    sRank++;
                }
            }
        }

        // Consolidate the output
        if (sRank < S.rows() || sRank > sMaxRank) {
            // Consolidate the output
            int sNewRank = Math.min(sRank, sMaxRank);
            final DoubleMatrix2D Scopy = S.like(sNewRank, S.columns());
            // Aggregate
            S.forEachNonZero(new IntIntDoubleFunction() {

                @Override
                public double apply(int first, int second, double value) {
                    int row = Math.min(first, sMaxRank - 1);
                    double norm2 = first < sMaxRank - 1 ? 1 : sRank - sMaxRank + 1;
                    Scopy.setQuick(row, second, Scopy.getQuick(row, second) + value / Math.sqrt(norm2));
                    return value;
                }
            });

            S = Scopy;
            sRank = sNewRank;
        }
    }

    public DoubleMatrix2D getS() {
        return S;
    }

    public int getRank() {
        return sRank;
    }
}
