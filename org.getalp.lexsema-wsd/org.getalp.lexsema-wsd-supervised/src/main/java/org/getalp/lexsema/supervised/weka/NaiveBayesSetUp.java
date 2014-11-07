/**
 *
 */
package org.getalp.lexsema.supervised.weka;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;

/**
 * @author tchechem
 */
public class NaiveBayesSetUp implements WekaClassifierSetUp {

    /* (non-Javadoc)
     * @see org.getalp.lexsema.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */
    @Override
    public Classifier setUpClassifier() {
        NaiveBayes network = new NaiveBayes();
        network.setUseKernelEstimator(true);
        network.setUseSupervisedDiscretization(true);
        return network;
    }

}
