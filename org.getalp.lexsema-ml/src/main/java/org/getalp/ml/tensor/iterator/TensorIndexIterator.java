package org.getalp.ml.tensor.iterator;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Iterator;
import java.util.stream.IntStream;

public final class TensorIndexIterator implements Iterator<int[]> {

    private final int[] shape;
    private final int[] currentPosition;
    private boolean next = true;
    private boolean start = true;

    private int currentDimension;
    private final IndexOrdering indexOrdering;

    public TensorIndexIterator(INDArray tensor) {
        this(tensor, IndexOrdering.INCREASING);
    }

    @SuppressWarnings("All")
    public TensorIndexIterator(INDArray tensor, IndexOrdering ordering) {
        shape = tensor.shape();
        currentPosition = new int[shape.length];
        IntStream.range(0, shape.length).forEach(i -> currentPosition[i] = 0);
        indexOrdering = ordering;
        if (indexOrdering == IndexOrdering.DECREASING) {
            currentDimension = shape.length - 1;
        } else {
            currentDimension = 0;
        }
    }

    @Override
    public synchronized boolean hasNext() {
        return next;
    }

    private void increaseCurrentDimension(int value) {
        if (indexOrdering == IndexOrdering.DECREASING) {
            currentDimension-=value;
        } else {
            currentDimension+=value;
        }
    }

    private void decreaseCurrentDimension(int value) {
        if (indexOrdering == IndexOrdering.DECREASING) {
            currentDimension+=value;
        } else {
            currentDimension-=value;
        }
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    @Override
    public synchronized int[] next() {
        if (start || !hasNext()) {
            start = false;
        } else {
            if (hasOverflown()) {
                int numberOverflown = 0;
                while (numberOverflown<shape.length && hasOverflown()) {
                    numberOverflown++;
                    increaseCurrentDimension(1);
                }
                if(numberOverflown==shape.length){
                    next = false;
                }
                if(currentDimension<shape.length && currentDimension>=0) {
                    currentPosition[currentDimension] = currentPosition[currentDimension] + 1;
                    while (numberOverflown > 0) {
                        decreaseCurrentDimension(1);
                        numberOverflown--;
                        currentPosition[currentDimension] = 0;
                    }
                }
            } else {
                currentPosition[currentDimension] = currentPosition[currentDimension] + 1;
            }
        }
        return currentPosition;
    }

    private boolean hasOverflown() {
        return hasOverflown(currentDimension);
    }

    private boolean hasOverflown(int dimension) {
        return currentPosition[dimension] + 1 >= shape[dimension];
    }

    public enum IndexOrdering {
        INCREASING, DECREASING
    }
}
