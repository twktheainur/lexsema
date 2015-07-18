/**
 *
 */
package org.getalp.ml.matrix.distance;

import org.getalp.ml.matrix.MatrixUtils;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * @author tchechem
 */
public class MahalanobisDistance extends Distance {


    public MahalanobisDistance() {
        super();
    }


    public double compute(INDArray pointA, INDArray pointB) {
        deNan(pointA);
        deNan(pointB);
        INDArray cov1 = MatrixUtils.covarianceMatrix(pointA);
        INDArray cov2 = MatrixUtils.covarianceMatrix(pointB);

        INDArray s1m = MatrixUtils.subtractMeanFromRows(pointA);
        INDArray s2m = MatrixUtils.subtractMeanFromRows(pointB);

        INDArray pooledCov = cov1.add(cov2).divi(2);

        INDArray averageVectorA = MatrixUtils.getColumnWiseMeanVector(s1m);
        INDArray averageVectorB = MatrixUtils.getColumnWiseMeanVector(s2m);

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
