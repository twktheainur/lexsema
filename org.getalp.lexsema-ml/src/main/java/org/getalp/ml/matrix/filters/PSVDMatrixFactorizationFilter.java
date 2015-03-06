package org.getalp.ml.matrix.filters;

import org.getalp.ml.matrix.factorization.PartialSingularValueDecompositionFactory;

public class PSVDMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public PSVDMatrixFactorizationFilter(int numberOfComponents) {
        super(new PartialSingularValueDecompositionFactory(), numberOfComponents);
    }
}
