/**
 *
 */
package org.getalp.ml.matrix.filters.frequency;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import org.getalp.ml.matrix.filters.Filter;


/**
 * @author tchechem
 */
public class FFTFilter implements Filter {

    private boolean enabled;

    public FFTFilter() {
        super();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public DoubleMatrix2D apply(DoubleMatrix2D signal) {
        if (!enabled) {
            return null;
        }
        int vc = Integer.highestOneBit(signal.columns() - 1);
        System.err.println(vc);
        for (int i = 0; i < signal.rows(); i++) {
            DoubleFFT_1D fft = new DoubleFFT_1D(signal.columns());
            fft.realForward(signal.viewColumn(i).toArray());
        }
        return signal;
    }

    public enum NormalizationType {
        UNIT_NORM, ZERO_MEAN_VAR
    }

}
