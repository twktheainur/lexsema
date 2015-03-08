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


import cern.colt.function.tdouble.IntIntDoubleFunction;
import cern.colt.matrix.tdouble.DoubleMatrix2D;

public final class SparseUtils {

    private SparseUtils() {
    }

    public static double dot(DoubleMatrix2D lhs, DoubleMatrix2D rhs) {
        final double dotProduct[] = {0};
        if (rhs.cardinality() < lhs.cardinality()) {
            DoubleMatrix2D temp = lhs;
            lhs = rhs;
            rhs = temp;
        }

        final DoubleMatrix2D rhsFinal = rhs;
        lhs.forEachNonZero(new IntIntDoubleFunction() {

            @Override
            public double apply(int first, int second, double value) {
                dotProduct[0] += value * rhsFinal.getQuick(first, second);
                return value;
            }
        });
        return dotProduct[0];
    }

    public static double norm2(DoubleMatrix2D m) {
        final double norm2[] = {0};
        m.forEachNonZero(new IntIntDoubleFunction() {

            @Override
            public double apply(int first, int second, double value) {
                norm2[0] += value * value;
                return value;
            }
        });
        return norm2[0];
    }

    public static double norm(DoubleMatrix2D m) {
        return Math.sqrt(norm2(m));
    }

    static public void zMult(DoubleMatrix2D A, DoubleMatrix2D B, DoubleMatrix2D C) {
        zMult(A, B, C, false, false);
    }

    static public void zMult(DoubleMatrix2D A, DoubleMatrix2D B, DoubleMatrix2D C, boolean transposeA, boolean transposeB) {
        if (transposeA) {
            CompressedSparseColumn aCsc = new CompressedSparseColumnAdapter(A);
            int[] colPointers = aCsc.getColumnPointers();
            int[] rowIndices = aCsc.getRowIndices();
            double[] data = aCsc.getData();

            for (int aColumn = 0; aColumn < A.columns(); ++aColumn) {
                if (transposeB) {
                    for (int bRow = 0; bRow < B.rows(); ++bRow) {
                        double sum = 0;
                        for (int k = colPointers[aColumn]; k < colPointers[aColumn + 1]; ++k) {
                            int aRow = rowIndices[k];
                            sum += data[k] * B.getQuick(bRow, aRow);
                        }
                        C.setQuick(aColumn, bRow, sum);
                    }
                } else {
                    for (int bColumn = 0; bColumn < B.columns(); ++bColumn) {
                        double sum = 0;
                        for (int k = colPointers[aColumn]; k < colPointers[aColumn + 1]; ++k) {
                            int aRow = rowIndices[k];
                            sum += data[k] * B.getQuick(aRow, bColumn);
                        }
                        C.setQuick(aColumn, bColumn, sum);
                    }
                }
            }
        } else {
            CompressedSparseRow aCsr = new CompressedSparseRowAdapter(A);
            int[] rowPointers = aCsr.getRowPointers();
            int[] colIndices = aCsr.getColumnIndices();
            double[] data = aCsr.getData();

            for (int aRow = 0; aRow < A.rows(); ++aRow) {
                if (transposeB) {
                    for (int bRow = 0; bRow < B.rows(); ++bRow) {
                        double sum = 0;
                        for (int k = rowPointers[aRow]; k < rowPointers[aRow + 1]; ++k) {
                            int aColumn = colIndices[k];
                            sum += data[k] * B.getQuick(bRow, aColumn);
                        }
                        C.setQuick(aRow, bRow, sum);
                    }
                } else {
                    for (int bColumn = 0; bColumn < B.columns(); ++bColumn) {
                        double sum = 0;
                        for (int k = rowPointers[aRow]; k < rowPointers[aRow + 1]; ++k) {
                            int aColumn = colIndices[k];
                            sum += data[k] * B.getQuick(aColumn, bColumn);
                        }
                        C.setQuick(aRow, bColumn, sum);
                    }
                }
            }
        }
    }

    public static int pairCount(DoubleMatrix2D lhs, final DoubleMatrix2D rhs) {
        final int[] pairCount = {0};
        lhs.forEachNonZero(new IntIntDoubleFunction() {

            @Override
            public double apply(int first, int second, double value) {
                double rhsValue = rhs.getQuick(first, second);
                if (rhsValue != 0) {
                    pairCount[0]++;
                }
                return value;
            }
        });
        return pairCount[0];
    }
}
