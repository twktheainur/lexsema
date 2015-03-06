package org.getalp.ml.matrix.filters;

import org.getalp.ml.matrix.factorization.NeuralICAMAtrixFactoizationFactory;

public class NeuralICAMatrixFactorizationFilter extends MatrixFactorizationFilter {
    public NeuralICAMatrixFactorizationFilter(int numberOfComponents) {
        super(new NeuralICAMAtrixFactoizationFactory(), numberOfComponents);
    }
}
