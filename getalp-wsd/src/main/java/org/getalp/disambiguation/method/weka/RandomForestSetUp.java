/**
 * 
 */
package org.getalp.disambiguation.method.weka;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;

/**
 * @author tchechem
 *
 */
public class RandomForestSetUp implements WekaClassifierSetUp {

	/* (non-Javadoc)
	 * @see org.getalp.disambiguation.method.weka.WekaClassifierSetUp#setUpClassifier()
	 */
	@Override
	public Classifier setUpClassifier() {
		RandomForest classifier = new RandomForest();
		classifier.setNumTrees(10);
        classifier.setMaxDepth(10);
		return classifier;
	}

}
