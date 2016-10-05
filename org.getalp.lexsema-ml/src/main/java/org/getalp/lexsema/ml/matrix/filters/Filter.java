/**
 *
 */
package org.getalp.lexsema.ml.matrix.filters;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.Serializable;

/**
 * @author tchechem
 */
public interface Filter extends Serializable{

    DoubleMatrix2D apply(DoubleMatrix2D signal);
    INDArray apply(INDArray signal);
    void setEnabled(boolean enabled);

}
