/**
 * 
 */
package org.getalp.disambiguation.method.weka;

import weka.classifiers.Classifier;

/**
 * @author tchechem
 *
 */
public interface WekaClassifierSetUp {
	public Classifier setUpClassifier() throws ClassifierSetUpException;
}
