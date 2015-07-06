package org.getalp.ml.matrix.filters.normalization;

import cern.colt.list.tdouble.DoubleArrayList;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;
import cern.jet.stat.tdouble.DoubleDescriptive;
import org.getalp.ml.matrix.filters.Filter;


public class ZSignificanceNormalizationFilter implements Filter {
    @Override
    public DoubleMatrix2D apply(DoubleMatrix2D data) {
        for(int i=0;i<data.rows();i++){
            boolean nonZero = false;
            DoubleMatrix1D vector = data.viewRow(i);
            DoubleArrayList vectorData = new DoubleArrayList(vector.toArray());
            double mean = DoubleDescriptive.mean(vectorData);
            double variance = DoubleDescriptive.sampleVariance(vectorData, mean);
            double standardDev = DoubleDescriptive.sampleStandardDeviation(vectorData.size(),variance);
            subtractMean(vector,mean);
            for(int j=0;j<data.columns();j++){
                if(Math.abs(data.getQuick(i,j))<standardDev){
                    data.setQuick(i,j,0d);
                }
            }
        }
        return null;
    }

    private void subtractMean(DoubleMatrix1D input, double mean){
        input.assign(DoubleFunctions.minus(mean));
    }

    @Override
    public void setEnabled(boolean enabled) {

    }
}
