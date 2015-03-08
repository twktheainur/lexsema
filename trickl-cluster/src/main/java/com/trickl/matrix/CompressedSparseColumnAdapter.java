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

public class CompressedSparseColumnAdapter implements CompressedSparseColumn {

    private int[] rowIndices;
    private int[] columnPointers;
    private double[] data;

    public CompressedSparseColumnAdapter(final DoubleMatrix2D mat) {
        // First get all the non-zero values in row major order
        // SparseDoubleMatrix is hash based, so we need to sort accordingly
        final int[] i = new int[1];
        i[0] = 0;
        final int[] columnMajorIndex = new int[mat.cardinality()];
        columnPointers = new int[mat.columns() + 1];
        rowIndices = new int[mat.cardinality()];
        data = new double[mat.cardinality()];
        mat.forEachNonZero(new IntIntDoubleFunction() {

            @Override
            public double apply(int first, int second, double value) {
                columnMajorIndex[i[0]] = second * mat.rows() + first;
                i[0] += 1;
                return value;
            }
        });
        Arrays.sort(columnMajorIndex);

        // Convert to CSR format
        int lastColumn = -1;
        for (int j = 0; j < columnMajorIndex.length; ++j) {
            int index = columnMajorIndex[j];
            int column = index / mat.rows();
            int row = index % mat.rows();
            while (column != lastColumn) {
                columnPointers[++lastColumn] = j;
            }
            data[j] = mat.getQuick(row, column);
            rowIndices[j] = row;
        }
        while (lastColumn < mat.columns()) {
            columnPointers[++lastColumn] = columnMajorIndex.length;
        }
    }

    @Override
    public int[] getRowIndices() {
        return rowIndices;
    }

    @Override
    public int[] getColumnPointers() {
        return columnPointers;
    }

    @Override
    public double[] getData() {
        return data;
    }
}
