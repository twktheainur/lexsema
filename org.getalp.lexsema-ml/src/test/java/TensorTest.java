import org.getalp.lexsema.ml.tensor.iterator.TensorIndexIterator;
import org.getalp.lexsema.ml.tensor.Tensors;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class TensorTest {
    @Test
    public void testTensorProductOnMatrices() throws Exception {
        INDArray tensorA = Nd4j.create(new double[][]{{1,2},{4,5}});
        INDArray tensorB = Nd4j.create(new double[][]{{7, 8}});
        logger.info("A -- \n{}",tensorA.toString());
        logger.info("B -- \n {}",tensorB.toString());
        logger.info("Result -- \n {}",Tensors.kroneckerProduct(tensorA, tensorB).toString());
    }

    @Test
    public void testTensorProductOnLargeTensors() throws Exception {
        INDArray tensorA = Nd4j.rand(new int[]{2, 3, 4});
        INDArray tensorB = Nd4j.rand(new int[]{4, 3, 4});

        logger.info("Result -- \n {}",Tensors.kroneckerProduct(tensorA, tensorB).toString());
    }

    private static final Logger logger = LoggerFactory.getLogger(TensorTest.class);

    @Test
    public void tensorIndexIteratorDecreasingTest() throws Exception {
        logger.info("-------------------Testing tensor index iterator in decreasing order-------------------");
        INDArray tensor = Nd4j.create(4,2,3);
        Iterator<int[]> tensorIndexIterator = new TensorIndexIterator(tensor, TensorIndexIterator.IndexOrdering.DECREASING);
        int position = 1;
        int[] currentIndex = tensorIndexIterator.next();
        assert currentIndex[0]==0 && currentIndex[1]==0 && currentIndex[2]==0;
        while (tensorIndexIterator.hasNext()){
            currentIndex = tensorIndexIterator.next();
            if(position==9){
                assert currentIndex[0]==1 && currentIndex[1]==1 && currentIndex[2]==0;
            }
            if(position==24){
                assert currentIndex[0]==3 && currentIndex[1]==1 && currentIndex[2]==2;
            }
            StringBuilder output = new StringBuilder();
            output.append("{");
            for(int i=0;i<currentIndex.length;i++){
                output.append(currentIndex[i]);
                if( i < currentIndex.length-1){
                    output.append(", ");
                }
            }
            output.append("}");
            logger.info(output.toString());
            position++;
        }
    }

    @Test
    public void tensorIndexIteratorIncreasingTest() throws Exception {
        logger.info("-------------------Testing tensor index iterator in decreasing order-------------------");
        INDArray tensor = Nd4j.create(4,2,3);
        Iterator<int[]> tensorIndexIterator = new TensorIndexIterator(tensor);

        int[] currentIndex = tensorIndexIterator.next();
        assert currentIndex[0]==0 && currentIndex[1]==0 && currentIndex[2]==0;

        int position = 1;
        while (tensorIndexIterator.hasNext()){
            currentIndex = tensorIndexIterator.next();
            if(position==9){
                assert currentIndex[0]==1 && currentIndex[1]==0 && currentIndex[2]==1;
            }
            if(position==24){
                assert currentIndex[0]==3 && currentIndex[1]==1 && currentIndex[2]==2;
            }
            StringBuilder output = new StringBuilder();
            output.append("{");
            for(int i=0;i<currentIndex.length;i++){
                output.append(currentIndex[i]);
                if( i < currentIndex.length-1){
                    output.append(", ");
                }
            }
            output.append("}");
            logger.info(output.toString());
            position++;
        }
    }
}
