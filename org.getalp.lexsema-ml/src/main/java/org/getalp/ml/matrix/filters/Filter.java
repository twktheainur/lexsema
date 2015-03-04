/**
 *
 */
package org.getalp.ml.matrix.filters;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

/**
 * @author tchechem
 */
public interface Filter {

    public DoubleMatrix2D apply(DoubleMatrix2D signal);

    public void setEnabled(boolean enabled);

}
