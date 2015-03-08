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
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;

/**
 * Provides efficient multiplication for general sparse matrices
 */
public class SparseMult2DFunction implements IntIntDoubleFunction {

    DoubleMatrix2D B;
    CompressedSparseRow Bcsr;
    CompressedSparseColumn Bcsc;
    boolean transposeA = false;
    boolean transposeB = false;
    private DoubleMatrix2D C;

    public SparseMult2DFunction(DoubleMatrix2D B, DoubleMatrix2D C) {
        this(B, C, false, false);
    }

    public SparseMult2DFunction(final DoubleMatrix2D B, DoubleMatrix2D C, boolean transposeA, boolean transposeB) {
        this.B = B;
        this.transposeA = transposeA;
        this.transposeB = transposeB;
        this.C = C;

        if (B instanceof SparseDoubleMatrix2D || C instanceof SparseDoubleMatrix2D) {
            if (transposeB) {
                Bcsc = new CompressedSparseColumnAdapter(B);
            } else {
                Bcsr = new CompressedSparseRowAdapter(B);
            }
        }
    }

    @Override
    public double apply(int first, int second, double third) {

        if (transposeA) {
            if (transposeB) {
                if (Bcsc != null) {
                    int[] columnPointers = Bcsc.getColumnPointers();
                    int[] rowIndices = Bcsc.getRowIndices();
                    double[] data = Bcsc.getData();
                    int column = first;
                    for (int k = columnPointers[column]; k < columnPointers[column + 1]; ++k) {
                        int row = rowIndices[k];
                        C.setQuick(second, row, C.getQuick(second, row) + data[k] * third);
                    }
                } else {
                    C.viewRow(second).assign(B.viewColumn(first), DoubleFunctions.plusMultSecond(third));
                }
            } else {
                if (Bcsr != null) {
                    int[] rowPointers = Bcsr.getRowPointers();
                    int[] colIndices = Bcsr.getColumnIndices();
                    double[] data = Bcsr.getData();
                    int row = first;
                    for (int k = rowPointers[row]; k < rowPointers[row + 1]; ++k) {
                        int column = colIndices[k];
                        C.setQuick(second, column, C.getQuick(second, column) + data[k] * third);
                    }
                } else {
                    C.viewRow(second).assign(B.viewRow(first), DoubleFunctions.plusMultSecond(third));
                }
            }
        } else {
            if (transposeB) {
                if (Bcsc != null) {
                    int[] columnPointers = Bcsc.getColumnPointers();
                    int[] rowIndices = Bcsc.getRowIndices();
                    double[] data = Bcsc.getData();

                    int column = second;
                    for (int k = columnPointers[column]; k < columnPointers[column + 1]; ++k) {
                        int row = rowIndices[k];
                        C.setQuick(first, row, C.getQuick(first, row) + data[k] * third);
                    }
                } else {
                    C.viewRow(first).assign(B.viewColumn(second), DoubleFunctions.plusMultSecond(third));
                }
            } else {
                if (Bcsr != null) {
                    int[] rowPointers = Bcsr.getRowPointers();
                    int[] colIndices = Bcsr.getColumnIndices();
                    double[] data = Bcsr.getData();
                    int row = second;
                    for (int k = rowPointers[row]; k < rowPointers[row + 1]; ++k) {
                        int column = colIndices[k];
                        C.setQuick(first, column, C.getQuick(first, column) + data[k] * third);
                    }
                } else {
                    C.viewRow(first).assign(B.viewRow(second), DoubleFunctions.plusMultSecond(third));
                }
            }
        }
        return third;
    }
}
