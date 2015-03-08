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
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;

public class MoorePenrosePseudoInverseBySVD implements MoorePenrosePseudoInverseAlgorithm {

    /**
     * The difference between 1 and the smallest exactly representable number
     * greater than one. Gives an upper bound on the relative error due to
     * rounding of floating point numbers.
     */
    public static double EPSILON = 1e-12;

    private SingularValueDecompositionAlgorithm svdAlgorithm = new ColtSvdAlgorithm();

    /*
     * Modified version of the original implementation by Kim van der Linde.
     * http://cio.nist.gov/esd/emaildir/lists/jama/msg01302.html
     */
    @Override
    public DoubleMatrix2D inverse(DoubleMatrix2D X) {
        svdAlgorithm.calculate(X);
        double[] singularValues = svdAlgorithm.getSingularValues();
        double tol = Math.max(X.columns(), X.rows()) * singularValues[0] * EPSILON;
        double[] singularValueReciprocals = new double[singularValues.length];
        for (int i = 0; i < singularValues.length; i++) {
            singularValueReciprocals[i] = Math.abs(singularValues[i]) < tol ? 0 : 1.0 / singularValues[i];
        }

        DoubleMatrix2D U = svdAlgorithm.getU();
        DoubleMatrix2D V = svdAlgorithm.getV();
        int min = Math.min(X.columns(), U.columns());
        double[][] inverse = new double[X.columns()][X.rows()];

        for (int i = 0; i < X.columns(); i++) {
            for (int j = 0; j < X.rows(); j++) {
                for (int k = 0; k < min; k++) {
                    inverse[i][j] += V.getQuick(i, k) * singularValueReciprocals[k] * U.getQuick(j, k);
                }
            }
        }

        return new DenseDoubleMatrix2D(inverse);
    }

    public SingularValueDecompositionAlgorithm getSvdAlgorithm() {
        return svdAlgorithm;
    }

    public void setSvdAlgorithm(SingularValueDecompositionAlgorithm svdAlgorithm) {
        this.svdAlgorithm = svdAlgorithm;
    }
}
