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
import cern.colt.matrix.tdouble.DoubleMatrix1D;

public class SparseMult1DFunction implements IntIntDoubleFunction {

    DoubleMatrix1D B;
    boolean transposeA = false;

    private DoubleMatrix1D C;

    public SparseMult1DFunction(DoubleMatrix1D B, DoubleMatrix1D C) {
        this(B, C, false);
    }

    public SparseMult1DFunction(DoubleMatrix1D B, DoubleMatrix1D C, boolean transposeA) {
        this.B = B;
        this.transposeA = transposeA;
        this.C = C;
    }

    @Override
    public double apply(int first, int second, double third) {
        if (transposeA) {
            C.setQuick(second, C.getQuick(second) + third * B.getQuick(first));
        } else {
            C.setQuick(first, C.getQuick(first) + third * B.getQuick(second));
        }
        return third;
    }
}
