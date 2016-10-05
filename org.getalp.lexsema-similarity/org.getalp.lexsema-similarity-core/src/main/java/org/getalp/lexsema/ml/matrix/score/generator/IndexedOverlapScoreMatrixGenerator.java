package org.getalp.lexsema.ml.matrix.score.generator;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;

import java.util.Collections;
import java.util.List;


public class IndexedOverlapScoreMatrixGenerator implements DenseScoreMatrixGenerator, SparseScoreMatrixGenerator {
    private final List<Integer> la;
    private final List<Integer> lb;
    private boolean invert;


    @SuppressWarnings("all")
    public IndexedOverlapScoreMatrixGenerator(final List<Integer> la, final List<Integer> lb) {
        this.la = Collections.unmodifiableList(la);
        this.lb = Collections.unmodifiableList(lb);
    }

    @Override
    public DenseDoubleMatrix2D generateDenseScoreMatrix() {
        DenseDoubleMatrix2D matrix = new DenseDoubleMatrix2D(la.size(), lb.size());
        generateScoreMatrix(matrix);
        return matrix;
    }

    @Override
    public SparseDoubleMatrix2D generateSparseScoreMatrix() {
        SparseDoubleMatrix2D matrix = new SparseDoubleMatrix2D(la.size(), lb.size());
        generateScoreMatrix(matrix);
        return matrix;
    }

    private void generateScoreMatrix(DoubleMatrix2D matrix2D) {
        if (invert) {
            matrix2D.assign(1);
        } else {
            matrix2D.assign(0);
        }
        int aSize = la.size();
        int bSize = lb.size();
        int i = 0;
        int j = 0;
        while (i < aSize && j < bSize) {
            if (la.get(i).compareTo(lb.get(j)) == 0 && !lb.get(j).equals(-1)) {
                if (invert) {
                    matrix2D.setQuick(i, j, 0d);
                } else {
                    matrix2D.setQuick(i, j, 1d);
                }
                i++;
                j++;
            } else if (la.get(i).compareTo(lb.get(j)) == -1) {
                i++;
            } else {
                j++;
            }
        }
    }

    @SuppressWarnings({"MethodReturnOfConcreteClass", "PublicMethodNotExposedInInterface", "BooleanParameter"})
    public IndexedOverlapScoreMatrixGenerator setInvert(boolean invert) {
        this.invert = invert;
        return this;
    }
}
