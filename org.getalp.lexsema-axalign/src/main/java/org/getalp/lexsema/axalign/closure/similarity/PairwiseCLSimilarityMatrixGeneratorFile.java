package org.getalp.lexsema.axalign.closure.similarity;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import org.getalp.lexsema.ml.matrix.filters.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PairwiseCLSimilarityMatrixGeneratorFile implements PairwiseSimilarityMatrixGenerator {

    private static Logger logger = LoggerFactory.getLogger(PairwiseSimilarityMatrixGenerator.class);

    private DoubleMatrix2D similarityMatrix = null;
    private String filename;

    public PairwiseCLSimilarityMatrixGeneratorFile(String filename) {
        this.filename = filename;
    }


    @Override
    public void generateMatrix(){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line = br.readLine();
            int row = 0;
            while(line!=null){
                String[] fields = line.split(",");
                if(similarityMatrix==null) {
                    similarityMatrix = new DenseDoubleMatrix2D(fields.length,fields.length);
                }
                for(int col=0;col < fields.length; col++){
                    similarityMatrix.setQuick(row,col,Double.valueOf(fields[col]));
                }
                line = br.readLine();
                row++;
            }
        } catch (IOException e){
            logger.error(e.getLocalizedMessage());
        }

    }

    @Override
    public DoubleMatrix2D getScoreMatrix(){
        return similarityMatrix;
    }

    @Override
    public DoubleMatrix2D getScoreMatrix(Filter filter) {
        DoubleMatrix2D processed = similarityMatrix.copy();
        filter.apply(processed);
        return processed;
    }
}
