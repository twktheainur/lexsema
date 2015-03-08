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


import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.decomposition.DenseDoubleSingularValueDecomposition;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;

public class ColtSvdAlgorithm implements SingularValueDecompositionAlgorithm {

    private DenseDoubleSingularValueDecomposition svd;
    private boolean transpose = false;

    @Override
    public void calculate(DoubleMatrix2D arg) {
        if (arg.rows() < arg.columns()) {
            // The Colt SVD implementation assumes #rows >= #columns
            transpose = true;
            arg = arg.viewDice();
        }
        svd = new DenseDoubleSingularValueDecomposition(arg, true, true);
    }

    @Override
    public double[] getSingularValues() {
        if (svd == null) return new double[0];
        return svd.getSingularValues();
    }

    @Override
    public DoubleMatrix2D getU() {
        if (svd == null) return new DenseDoubleMatrix2D(0, 0);
        return transpose ? svd.getV() : svd.getU();
    }

    @Override
    public DoubleMatrix2D getV() {
        if (svd == null) return new DenseDoubleMatrix2D(0, 0);
        return transpose ? svd.getU() : svd.getV();
    }

}
