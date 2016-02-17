package org.getalp.ml.tensor;

import org.getalp.ml.tensor.opertor.BinaryTensorOperator;
import org.getalp.ml.tensor.opertor.KroneckerProductOperator;
import org.getalp.ml.tensor.opertor.KroneckerTensorProductOperator;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * A class implementing a number of tensor operations with an ND4J backend
 */
public final class Tensors {
    private Tensors(){
    }

    /**
     * Computes the kronecker product between two arbitrary dimension tensors and produces an result tensor of the same
     * order.
     * {@see }
     * @param tensorA The first tensor
     * @param tensorB The second tensor
     * @return The tensor resulting from the product of the two tensors
     */
    public static INDArray kroneckerProduct(INDArray tensorA, INDArray tensorB){
        BinaryTensorOperator binaryTensorOperator = new KroneckerProductOperator();
        return binaryTensorOperator.operator(tensorA,tensorB);
    }

    /**
     * Computes the kronecker product between two arbitrary dimension and encodes the result as an additional dimension
     * of the result tensor compared to the input tensors.
     * {@see http://www.cs.cornell.edu/cv/OtherPdf/Mat2Ten.pdf}
     * @param tensorA The first tensor
     * @param tensorB The second tensor
     * @return The tensor resulting from the product of the two tensors
     */
    public static INDArray kroneckerTensorProduct(INDArray tensorA, INDArray tensorB){
        BinaryTensorOperator binaryTensorOperator = new KroneckerTensorProductOperator();
        return binaryTensorOperator.operator(tensorA,tensorB);
    }

}
