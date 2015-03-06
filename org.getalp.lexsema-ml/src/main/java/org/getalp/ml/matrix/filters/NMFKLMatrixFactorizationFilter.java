package org.getalp.ml.matrix.filters;

import org.getalp.ml.matrix.factorization.NonnegativeMatrixFactorizationKLFactory;

public class NMFKLMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public NMFKLMatrixFactorizationFilter(int numberOfComponents) {
        super(new NonnegativeMatrixFactorizationKLFactory(), numberOfComponents);
    }
}
