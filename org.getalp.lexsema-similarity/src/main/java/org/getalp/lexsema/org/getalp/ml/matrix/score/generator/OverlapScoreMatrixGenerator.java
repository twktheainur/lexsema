package org.getalp.lexsema.org.getalp.ml.matrix.score.generator;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import com.wcohen.ss.AbstractStringDistance;
import org.getalp.ml.matrix.score.generator.DenseScoreMatrixGenerator;
import org.getalp.ml.matrix.score.generator.SparseScoreMatrixGenerator;

import java.util.Collections;
import java.util.List;


public class OverlapScoreMatrixGenerator implements DenseScoreMatrixGenerator, SparseScoreMatrixGenerator {
    private static final double THRESHOLD = .5d;
    private final AbstractStringDistance distance;
    private final List<String> la;
    private final List<String> lb;
    private boolean invert;

    public OverlapScoreMatrixGenerator(final AbstractStringDistance distance, final List<String> la, final List<String> lb) {
        this.distance = distance;
        this.la = Collections.unmodifiableList(la);
        this.lb = Collections.unmodifiableList(lb);
    }

    @SuppressWarnings("all")
    public OverlapScoreMatrixGenerator(final List<String> la, final List<String> lb) {
        this.la = Collections.unmodifiableList(la);
        this.lb = Collections.unmodifiableList(lb);
        distance = null;
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
        matrix2D.assign(-1);
        for (int i = 0; i < la.size(); i++) {
            String a = la.get(i);
            for (int j = 0; j < lb.size(); j++) {
                String b = lb.get(j);
                double value = 1d;
                if (distance != null) {
                    value = distance.score(distance.prepare(a), distance.prepare(b));
                } else if (a.equals(b)) {
                    value = 1;
                }
                if (invert) {
                    value = 1 - value;
                }
                matrix2D.setQuick(i, j, value);
            }
        }
    }


    @SuppressWarnings({"MethodReturnOfConcreteClass", "PublicMethodNotExposedInInterface", "BooleanParameter"})
    public OverlapScoreMatrixGenerator setFuzzyMatching(boolean fuzzyMatching) {
        return this;
    }

    @SuppressWarnings({"MethodReturnOfConcreteClass", "PublicMethodNotExposedInInterface", "BooleanParameter"})
    public OverlapScoreMatrixGenerator setInvert(boolean invert) {
        this.invert = invert;
        return this;
    }
}
