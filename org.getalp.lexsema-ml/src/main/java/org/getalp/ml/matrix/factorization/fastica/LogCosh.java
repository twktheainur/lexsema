/* 
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to <http://unlicense.org/>
 */

package org.getalp.ml.matrix.factorization.fastica;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;


/**
 * Default function used in FastICA to approxmate neg-entropy.
 *
 * @author Chris Gearhart <cgearhart3@gatech.edu>
 */
public class LogCosh implements NegativeEntropyEstimator {

    // Scaling factor
    private final double alpha;
    // Element-wise application of the neg-entropy function applied to data matrix
    private DoubleMatrix2D gx;
    // Column-wise average of the first derivative of gx; i.e., the average of 1 - gx[i]**2
    private DoubleMatrix1D g_x;

    public LogCosh(double alpha) {
        this.alpha = alpha;
    }

    public LogCosh() {
        this(1.);
    }

    /**
     * @param x - {@link cern.colt.matrix.tdouble.DoubleMatrix2D} of column vectors for each feature
     */
    @Override
    public void estimate(DoubleMatrix2D x) {
        double val;
        double tmp;
        int m = x.rows();
        int n = x.columns();

        gx = new DenseDoubleMatrix2D(m, n);
        g_x = new DenseDoubleMatrix1D(n);
        for (int j = 0; j < n; j++) {
            tmp = 0;
            for (int i = 0; i < m; i++) {
                val = Math.tanh(x.get(i, j));
                gx.set(i, j, val);
                tmp += alpha * (1 - Math.pow(val, 2));
            }
            g_x.set(j, tmp / m);
        }

    }

    @Override
    public DoubleMatrix2D getGx() {
        return gx;
    }

    @Override
    public DoubleMatrix1D getG_x() {
        return g_x;
    }
}
