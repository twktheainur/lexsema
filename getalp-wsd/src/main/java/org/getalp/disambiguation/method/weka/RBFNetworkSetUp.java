/**
 * 
 */
package org.getalp.disambiguation.method.weka;

import weka.classifiers.Classifier;
import weka.classifiers.functions.RBFNetwork;

/**
 * @author tchechem
 *
 */
public class RBFNetworkSetUp implements WekaClassifierSetUp {

	/* (non-Javadoc)
	 * @see org.getalp.disambiguation.method.weka.WekaClassifierSetUp#setUpClassifier()
	 */
	@Override
	public Classifier setUpClassifier() {
		RBFNetwork network = new RBFNetwork();
		network.setMaxIts(-1);
        network.setNumClusters(20);
        //network.setRidge();
		return network;
	}

}
