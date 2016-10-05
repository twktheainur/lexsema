package org.getalp.lexsema.ml.matrix.filters;

import org.getalp.lexsema.ml.matrix.factorization.PartialSingularValueDecompositionFactory;

public class PSVDMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public PSVDMatrixFactorizationFilter(int numberOfComponents) {
        super(new PartialSingularValueDecompositionFactory(), numberOfComponents);
    }
}
