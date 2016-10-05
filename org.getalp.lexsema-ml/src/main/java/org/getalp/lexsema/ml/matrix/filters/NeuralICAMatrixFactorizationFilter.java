package org.getalp.lexsema.ml.matrix.filters;

import org.getalp.lexsema.ml.matrix.factorization.NeuralICAMAtrixFactoizationFactory;

public class NeuralICAMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public NeuralICAMatrixFactorizationFilter(int numberOfComponents) {
        super(new NeuralICAMAtrixFactoizationFactory(), numberOfComponents);
    }
}
