package org.getalp.ml.tensor.opertor;

import org.nd4j.linalg.api.ndarray.INDArray;

@FunctionalInterface
public interface BinaryTensorOperator {
    INDArray operator(INDArray tensorA, INDArray tensorB);
}
