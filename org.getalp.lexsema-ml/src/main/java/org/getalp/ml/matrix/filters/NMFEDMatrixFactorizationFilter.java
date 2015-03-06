package org.getalp.ml.matrix.filters;

import org.getalp.ml.matrix.factorization.NonnegativeMatrixFactorizationEDFactory;

public class NMFEDMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public NMFEDMatrixFactorizationFilter(int numberOfComponents) {
        super(new NonnegativeMatrixFactorizationEDFactory(), numberOfComponents);
    }
}
