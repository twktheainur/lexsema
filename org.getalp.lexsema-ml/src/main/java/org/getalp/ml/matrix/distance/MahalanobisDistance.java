/**
 *
 */
package org.getalp.ml.matrix.distance;

import org.getalp.ml.matrix.Matrices;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author tchechem
 */
public class MahalanobisDistance extends Distance {


    @SuppressWarnings("FeatureEnvy")
    @Override
    public double compute(INDArray pointA, INDArray pointB) {
        deNan(pointA);
        deNan(pointB);
        INDArray cov1 = Matrices.covarianceMatrix(pointA);
        INDArray cov2 = Matrices.covarianceMatrix(pointB);

        INDArray s1m = Matrices.subtractMeanFromRows(pointA);
        INDArray s2m = Matrices.subtractMeanFromRows(pointB);

        INDArray pooledCov = cov1.add(cov2).divi(2);

        INDArray averageVectorA = Matrices.getColumnWiseMeanVector(s1m);
        INDArray averageVectorB = Matrices.getColumnWiseMeanVector(s2m);

        double distanceSum = Math.sqrt(
                averageVectorA
                        .mmul(pooledCov)
                        .mmul(averageVectorB.transpose())
                        .getDouble(0));
        if(Double.isInfinite(distanceSum) || Double.isNaN(distanceSum)){
            distanceSum = 0;
        }
        return distanceSum;
    }


}
