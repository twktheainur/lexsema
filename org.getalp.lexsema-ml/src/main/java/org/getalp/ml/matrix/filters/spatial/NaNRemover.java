package org.getalp.ml.matrix.filters.spatial;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.ml.matrix.Matrices;
import org.getalp.ml.matrix.filters.Filter;
import org.nd4j.linalg.api.ndarray.INDArray;

public class NaNRemover implements Filter {

    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public DoubleMatrix2D apply(DoubleMatrix2D signal) {
        if (!enabled) {
            return null;
        }
        DoubleMatrix2D mat = signal;
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.columns(); j++) {
                if (Double.isNaN(mat.getQuick(i, j))) {
                    mat.setQuick(i, j, 0);
                }
            }
        }
        return signal;
    }
    @Override
    public INDArray apply(INDArray signal) {
        return Matrices.toINDArray(apply(Matrices.toColtMatrix(signal)));
    }

}
