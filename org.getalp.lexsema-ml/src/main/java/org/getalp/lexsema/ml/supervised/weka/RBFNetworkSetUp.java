/**
 *
 */
package org.getalp.lexsema.ml.supervised.weka;

import weka.classifiers.functions.RBFNetwork;

/**
 * @author tchechem
 */
public class RBFNetworkSetUp implements WekaClassifierSetUp {

    /* (non-Javadoc)
     * @see org.getalp.lexsema.ml.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */
    @Override
    public weka.classifiers.Classifier setUpClassifier() {
        RBFNetwork network = new RBFNetwork();
        network.setMaxIts(-1);
        network.setNumClusters(20);
        //network.setRidge();
        return network;
    }

}
