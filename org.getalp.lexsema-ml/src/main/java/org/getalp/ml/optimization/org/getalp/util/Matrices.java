package org.getalp.ml.optimization.org.getalp.util;


import cern.colt.matrix.tdouble.DoubleMatrix2D;

import java.io.PrintWriter;

public final class Matrices {
    private Matrices(){}

    public static void matrixCSVWriter(PrintWriter pw, DoubleMatrix2D matrix) {
        for (int row = 0; row < matrix.rows(); row++) {
            for (int col = 0; col < matrix.columns(); col++) {
                if (col < matrix.columns() - 1) {
                    pw.print(String.format("%s,", matrix.getQuick(row, col)));
                } else {
                    pw.println(matrix.getQuick(row, col));
                }
            }
        }
        pw.flush();
    }
}
