/**
 * 
 */
package org.getalp.disambiguation.method.weka;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesianLogisticRegression;

/**
 * @author tchechem
 *
 */
public class BayesianLogisticRegressionSetUp implements WekaClassifierSetUp {

	/* (non-Javadoc)
	 * @see org.getalp.disambiguation.method.weka.WekaClassifierSetUp#setUpClassifier()
	 */
	@Override
	public Classifier setUpClassifier() {
		BayesianLogisticRegression classifier = new BayesianLogisticRegression();
		classifier.setNormalizeData(true);
		return classifier;
	}

}
