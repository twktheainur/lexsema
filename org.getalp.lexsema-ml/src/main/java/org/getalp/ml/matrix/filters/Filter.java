/**
 *
 */
package org.getalp.ml.matrix.filters;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author tchechem
 */
public interface Filter {

    public DoubleMatrix2D apply(DoubleMatrix2D signal);
    public INDArray apply(INDArray signal);
    public void setEnabled(boolean enabled);

}
