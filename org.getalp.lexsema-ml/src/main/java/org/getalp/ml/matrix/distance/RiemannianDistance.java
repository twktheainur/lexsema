package org.getalp.ml.matrix.distance;

import org.getalp.ml.matrix.Matrices;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.eigen.Eigen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tchechem
 */
public class RiemannianDistance extends Distance {
    private static final Logger logger = LoggerFactory.getLogger(RiemannianDistance.class);

    public RiemannianDistance() {
        super();
    }

    @Override
    public double compute(INDArray pointA, INDArray pointB) {
        double distanceSum = 0.000000000000000001d;
        try {
            deNan(pointA);
            deNan(pointB);

            INDArray cov1 = Matrices.covarianceMatrix(pointA);
            INDArray cov2 = Matrices.covarianceMatrix(pointB);

            INDArray tmp = Matrices.inverse(cov1).mmul(cov2);
            INDArray eigenvalues = Eigen.eigenvalues(tmp);
            for (int i = 0; i < eigenvalues.columns(); i++) {
                double val = Math.abs(eigenvalues.getDouble(i));
                if(val>0) {
                    distanceSum += Math.log(val) * Math.log(val);
                }
            }
        } catch (IllegalArgumentException e){
            //if(logger.isErrorEnabled()){
                logger.info("{} Invalid non-invertible matrix!", e.getLocalizedMessage());
           // }
        }
        if(Double.isInfinite(distanceSum) || Double.isNaN(distanceSum)){
            distanceSum = 1;
        }
        return Math.sqrt(distanceSum);
    }
}
