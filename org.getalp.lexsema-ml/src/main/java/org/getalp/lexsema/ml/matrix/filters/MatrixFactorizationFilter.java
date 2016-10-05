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

package org.getalp.lexsema.ml.matrix.filters;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.ml.matrix.Matrices;
import org.getalp.lexsema.ml.matrix.factorization.MatrixFactorization;
import org.getalp.lexsema.ml.matrix.factorization.MatrixFactorizationFactory;
import org.nd4j.linalg.api.ndarray.INDArray;


public class MatrixFactorizationFilter implements Filter {

    private final MatrixFactorizationFactory factorizationFactory;
    private int numberOfComponents = -1;
    private boolean enabled = true;

    public MatrixFactorizationFilter(MatrixFactorizationFactory factorizationFactory, int numberOfComponents) {
        this.factorizationFactory = factorizationFactory;
        this.numberOfComponents = numberOfComponents;
    }

    public MatrixFactorizationFilter(MatrixFactorizationFactory factorizationFactory) {
        this.factorizationFactory = factorizationFactory;
    }

    @Override
    public DoubleMatrix2D apply(DoubleMatrix2D signal) {
        if (enabled) {
            factorizationFactory.setK(numberOfComponents);
            MatrixFactorization factorization = factorizationFactory.factorize(signal);
            DoubleMatrix2D result = projectedMatrix(factorization);
            //if (numberOfComponents > 0) {
              //  if (numberOfComponents <= result.columns()) {
                  //  result = result.viewPart(0, 0, result.rows(), numberOfComponents);
                //}
            //}
            return result;
        }
        return signal;
    }

    @Override
    public INDArray apply(INDArray signal) {
        return Matrices.toINDArray(apply(Matrices.toColtMatrix(signal)));
    }

    private DoubleMatrix2D projectedMatrix(MatrixFactorization matrixFactorization){
        return matrixFactorization.getV();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = true;
    }


}
