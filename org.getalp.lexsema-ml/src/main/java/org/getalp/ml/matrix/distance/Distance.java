/**
 * 
 */
package org.getalp.ml.matrix.distance;

import org.nd4j.linalg.api.ndarray.INDArray;


/**
 * @author tchechem
 * 
 */
@SuppressWarnings("ClassWithoutLogger")
public abstract class Distance {

	protected Distance() {
		super();
	}

    public abstract double compute(INDArray pointA, INDArray pointB);

	protected void deNan(INDArray toDenan){
		INDArray linear = toDenan.linearView();
		for(int i=0;i<linear.columns();i++){
			if(Double.isNaN(linear.getDouble(i)) || Double.isInfinite(linear.getDouble(i))){
				linear.putScalar(i,0d);
			}
		}
	}

}
