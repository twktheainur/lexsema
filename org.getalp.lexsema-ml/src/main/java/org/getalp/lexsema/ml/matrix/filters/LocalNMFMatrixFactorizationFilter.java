package org.getalp.lexsema.ml.matrix.filters;

import org.getalp.lexsema.ml.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;

public class LocalNMFMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public LocalNMFMatrixFactorizationFilter(int numberOfComponents) {
        super(new LocalNonnegativeMatrixFactorizationFactory(), numberOfComponents);
    }
}
