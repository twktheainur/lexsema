package org.getalp.lexsema.ml.tensor.opertor;


import org.getalp.lexsema.ml.tensor.iterator.TensorIndexIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Iterator;

public class KroneckerProductOperator implements BinaryTensorOperator{

    /**
     * Computes the tensor product between two arbitrary dimension tensors.
     * {@see }
     * @param tensorA The first tensor
     * @param tensorB The second tensor
     * @return The tensor resulting from the product of the two tensors
     */
    @Override
    public INDArray operator(INDArray tensorA, INDArray tensorB) {
        int[] aShape = tensorA.shape();
        int[] bShape = tensorB.shape();
        int[] resultShape = computeProductResultShape(aShape, bShape);
        int[] currentIndexA;
        int[] currentIndexB;
        int[] currentIndexResult = new int[resultShape.length];
        INDArray result = Nd4j.create(resultShape);

        Iterator<int[]> tensorIndexIteratorA = new TensorIndexIterator(tensorA);
        Iterator<int[]> tensorIndexIteratorB = new TensorIndexIterator(tensorB, TensorIndexIterator.IndexOrdering.DECREASING);
        while(tensorIndexIteratorA.hasNext()){
            currentIndexA = tensorIndexIteratorA.next();
            while (tensorIndexIteratorB.hasNext()){
                currentIndexB = tensorIndexIteratorB.next();
                computeResultIndex(currentIndexA, currentIndexB, aShape, bShape, currentIndexResult);
                result.putScalar(currentIndexResult, tensorA.getDouble(currentIndexA) * tensorB.getDouble(currentIndexB));
            }
            tensorIndexIteratorB = new TensorIndexIterator(tensorB);

        }
        return result;
    }



    private static void computeResultIndex(int[] currentIndexA, int[] currentIndexB, int[] shapeA, int[] shapeB,int[] currentIndexResult){
        int aLength = shapeA.length;
        int bLength = shapeB.length;
        int maxLength = Math.max(aLength, bLength);
        for(int i=0;i<maxLength;i++){
            int aIndex;
            int bIndex;
            int bShapeVal;
            if(i>=aLength){
                aIndex = 0;
            } else {
                aIndex = currentIndexA[i];
            }
            if(i>=bLength){
                bIndex = 0;
                bShapeVal = 1;
            } else {
                bIndex = currentIndexB[i];
                bShapeVal = shapeB[i];
            }
            currentIndexResult[i] = aIndex*bShapeVal + bIndex;
        }
    }


    private static int[] computeProductResultShape(int[] aShape, int[] bShape){
        int aLength = aShape.length;
        int bLength = bShape.length;
        int maxLength = Math.max(aLength, bLength);
        int[] resultShape = new int[maxLength];
        for(int i=0; i< maxLength;i++){
            if(i<aLength && i<bLength) {
                resultShape[i] = aShape[i]*bShape[i];
            } else if (i<aLength && i >= bLength ){
                resultShape[i] = aShape[i];
            } else if (i < bLength && i >=aLength){
                resultShape[i] = bShape[i];
            }
        }
        return resultShape;
    }
}
