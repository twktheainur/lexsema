package org.getalp.lexsema.ml.matrix.filters;

import org.getalp.lexsema.ml.matrix.factorization.NonnegativeMatrixFactorizationKLFactory;

public class NMFKLMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public NMFKLMatrixFactorizationFilter(int numberOfComponents) {
        super(new NonnegativeMatrixFactorizationKLFactory(), numberOfComponents);
    }
}
