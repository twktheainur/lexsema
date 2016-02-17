/**
 *
 */
package org.getalp.ml.matrix.filters.normalization;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DoubleStatistic;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.jet.math.tdouble.DoubleFunctions;
import cern.jet.stat.tdouble.DoubleDescriptive;
import org.getalp.ml.matrix.Matrices;
import org.getalp.ml.matrix.filters.Filter;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author tchechem
 */
public class NormalizationFilter implements Filter {

    boolean enabled = true;

    private NormalizationType normalizationType;

    public NormalizationFilter(NormalizationType normalizationType) {
        super();
        this.normalizationType = normalizationType;
    }

    public NormalizationFilter() {
        super();
        normalizationType = NormalizationType.UNIT_NORM;

    }

    @Override
    public DoubleMatrix2D apply(DoubleMatrix2D signal) {
        if (!enabled) {
            return null;
        }
        if (normalizationType.equals(NormalizationType.UNIT_NORM)) {
            for (int i = 0; i < signal.rows(); i++) {
                signal.viewRow(i).normalize();
            }
        } else {
            DoubleMatrix1D mean = getMeanVector(signal);
            for (int i = 0; i < signal.rows(); i++) {
                DoubleMatrix1D mean_ref = mean;
                if (signal.viewRow(i).size() < mean.size()) {
                    mean_ref = mean.viewPart(0, (int) signal.viewRow(i).size());
                }
            }
            DoubleMatrix1D varvect = getVarianceVector(signal);
            for (int i = 0; i < signal.rows(); i++) {
                DoubleMatrix1D row = signal.viewRow(i);
                double var = varvect.getQuick(i);
                double k = Math.sqrt(1.0 / var);
                row.assign(DoubleFunctions.mult(k));
            }
        }
        return signal;
    }

    public DoubleMatrix1D getMeanVector(DoubleMatrix2D signal) {
        DoubleMatrix1D mean = new DenseDoubleMatrix1D(signal.rows());
        for (int i = 0; i < signal.rows(); i++) {
            mean.setQuick(i, signal.viewRow(i).zSum() / signal.columns());
        }
        return mean;
    }

    public DoubleMatrix1D getVarianceVector(DoubleMatrix2D signal) {
        DoubleMatrix1D var = new DenseDoubleMatrix1D(signal.rows());
        for (int i = 0; i < signal.rows(); i++) {
            DoubleMatrix1D row = signal.viewRow(i);
            var.setQuick(i, DoubleDescriptive.variance(signal.columns(), row.zSum(), row.zDotProduct(row)));
        }
        return var;
    }

    public DoubleMatrix2D getCoVarianceMatrix(DoubleMatrix2D signal) {
        return DoubleStatistic.covariance(signal.viewDice());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public INDArray apply(INDArray signal) {
        return Matrices.toINDArray(apply(Matrices.toColtMatrix(signal)));
    }


    public enum NormalizationType {
        UNIT_NORM, ZERO_MEAN_VAR
    }
}
