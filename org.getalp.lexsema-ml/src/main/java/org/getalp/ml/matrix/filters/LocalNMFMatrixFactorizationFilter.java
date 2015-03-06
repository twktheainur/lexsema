package org.getalp.ml.matrix.filters;

import org.getalp.ml.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;

public class LocalNMFMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public LocalNMFMatrixFactorizationFilter(int numberOfComponents) {
        super(new LocalNonnegativeMatrixFactorizationFactory(), numberOfComponents);
    }
}
