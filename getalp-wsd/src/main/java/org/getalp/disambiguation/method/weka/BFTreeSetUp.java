/**
 * 
 */
package org.getalp.disambiguation.method.weka;

import weka.classifiers.Classifier;
import weka.classifiers.trees.BFTree;

/**
 * @author tchechem
 *
 */
public class BFTreeSetUp implements WekaClassifierSetUp {

	/* (non-Javadoc)
	 * @see org.getalp.disambiguation.method.weka.WekaClassifierSetUp#setUpClassifier()
	 */
	@Override
	public Classifier setUpClassifier() {
		BFTree classifier = new BFTree();
		classifier.setUseGini(true);
		return classifier;
	}

}
