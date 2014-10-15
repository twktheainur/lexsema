/**
 * 
 */
package org.getalp.disambiguation.method.weka;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

/**
 * @author tchechem
 *
 */
public class NaiveBayesSetUp implements WekaClassifierSetUp {

	/* (non-Javadoc)
	 * @see org.getalp.disambiguation.method.weka.WekaClassifierSetUp#setUpClassifier()
	 */
	@Override
	public Classifier setUpClassifier() {
		NaiveBayes network = new NaiveBayes();
		network.setUseKernelEstimator(true);
        network.setUseSupervisedDiscretization(true);
		return network;
	}

}
