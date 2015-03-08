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

import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;

import java.util.Arrays;

public class CompressedSparseColumnMatrix implements CompressedSparseColumn {

    private int[] columnPointers;
    private int[] rowIndices = new int[0];
    private double[] data = new double[0];
    private int rows;

    public CompressedSparseColumnMatrix(int rows, int columns) {
        this.rows = rows;
        columnPointers = new int[columns + 1];
    }

    public int columns() {
        return columnPointers.length - 1;
    }

    public int rows() {
        return rows;
    }

    @Override
    public int[] getColumnPointers() {
        return columnPointers;
    }

    @Override
    public int[] getRowIndices() {
        return rowIndices;
    }

    @Override
    public double[] getData() {
        return data;
    }

    public void set(int row, int column, double value) {
        int dataStart = columnPointers[column];
        int dataEnd = columnPointers[column + 1];
        int k = Arrays.binarySearch(rowIndices, dataStart, dataEnd, row);

        if (k >= 0) {
            data[k] = value;
        } else {
            k = insert(row, column);
            data[k] = value;
        }
    }

    public double get(int row, int column) {
        int dataStart = columnPointers[column];
        int dataEnd = columnPointers[column + 1];
        int k = Arrays.binarySearch(rowIndices, dataStart, dataEnd, row);

        if (k >= 0) {
            return data[k];
        } else {
            return 0;
        }
    }

    public SparseDoubleMatrix2D viewColumn(int column) {
        int dataStart = columnPointers[column];
        int dataEnd = columnPointers[column + 1];
        SparseDoubleMatrix2D mat = new SparseDoubleMatrix2D(rows, 1);
        for (int k = dataStart; k < dataEnd; ++k) {
            int row = rowIndices[k];
            mat.setQuick(row, 0, data[k]);
        }
        return mat;
    }

    /* Due to the poor performance of this routine, it may be worth
     * making this class immutable.
     */
    private int insert(int row, int column) {
        int k = data.length;

        int dataStart = columnPointers[column];
        int dataEnd = columnPointers[column + 1];
        k = -Arrays.binarySearch(rowIndices, dataStart, dataEnd, row) - 1;

        for (int j = column + 1; j < columnPointers.length; ++j) {
            columnPointers[j]++;
        }

        double dataNew[] = Arrays.copyOf(data, data.length + 1);
        int rowIndicesNew[] = Arrays.copyOf(rowIndices, data.length + 1);
        for (int m = data.length; m > k; --m) {
            dataNew[m] = dataNew[m - 1];
            rowIndicesNew[m] = rowIndicesNew[m - 1];
        }
        data = dataNew;
        rowIndices = rowIndicesNew;
        rowIndices[k] = row;

        return k;
    }
}
