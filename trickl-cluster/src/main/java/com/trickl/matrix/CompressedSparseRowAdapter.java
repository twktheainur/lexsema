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

import java.util.Arrays;

public class CompressedSparseRowAdapter implements CompressedSparseRow {

    private int[] rowPointers;
    private int[] columnIndices;
    private double[] data;

    public CompressedSparseRowAdapter(final DoubleMatrix2D mat) {
        // First get all the non-zero values in row major order
        int cardinality = mat.cardinality();
        final int[] rowMajorIndex = new int[cardinality];
        allocateSpace(mat, cardinality);

        generateRowMajorIndex(mat, rowMajorIndex);

        // Convert to CSR format
        convertToCSR(mat, rowMajorIndex);
    }

    private void allocateSpace(final DoubleMatrix2D mat, int cardinality) {
        rowPointers = new int[mat.rows() + 1];
        columnIndices = new int[cardinality];
        data = new double[cardinality];
    }

    private void generateRowMajorIndex(final DoubleMatrix2D mat, final int[] rowMajorIndex) {
        final int[] i = new int[1];
        i[0] = 0;
        mat.forEachNonZero(new IntIntDoubleFunction() {

            @Override
            public double apply(int first, int second, double value) {
                rowMajorIndex[i[0]] = first * mat.columns() + second;
                i[0] += 1;
                return value;
            }
        });
        Arrays.sort(rowMajorIndex);
    }

    private void convertToCSR(final DoubleMatrix2D mat, final int[] rowMajorIndex) {
        // Convert to CSR format
        int lastRow = -1;
        for (int j = 0; j < rowMajorIndex.length; ++j) {
            int index = rowMajorIndex[j];
            int row = index / mat.columns();
            int column = index % mat.columns();
            while (row != lastRow) {
                rowPointers[++lastRow] = j;
            }
            data[j] = mat.getQuick(row, column);
            columnIndices[j] = column;
        }
        while (lastRow < mat.rows()) {
            rowPointers[++lastRow] = rowMajorIndex.length;
        }
    }

    @Override
    public int[] getRowPointers() {
        return rowPointers;
    }

    @Override
    public int[] getColumnIndices() {
        return columnIndices;
    }

    @Override
    public double[] getData() {
        return data;
    }
}
