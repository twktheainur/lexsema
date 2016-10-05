package org.getalp.lexsema.ml.matrix.filters;

import org.getalp.lexsema.ml.matrix.factorization.NonnegativeMatrixFactorizationEDFactory;

public class NMFEDMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public NMFEDMatrixFactorizationFilter(int numberOfComponents) {
        super(new NonnegativeMatrixFactorizationEDFactory(), numberOfComponents);
    }
}
