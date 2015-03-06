package org.getalp.ml.matrix.filters;

import org.getalp.ml.matrix.factorization.NonnegativeMatrixFactorizationKLFactory;

public class NMFEDMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public NMFEDMatrixFactorizationFilter(int numberOfComponents) {
        super(new NonnegativeMatrixFactorizationKLFactory(), numberOfComponents);
    }
}
