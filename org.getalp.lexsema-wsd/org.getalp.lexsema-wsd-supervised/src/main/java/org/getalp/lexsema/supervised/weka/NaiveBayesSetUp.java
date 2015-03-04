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

    private boolean useKernelEstimator = true;
    private boolean useSupervisedDiscretization = true;

    /* (non-Javadoc)
     * @see org.getalp.lexsema.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */

    public NaiveBayesSetUp(boolean useKernelEstimator, boolean useSupervisedDiscretization) {

        this.useKernelEstimator = useKernelEstimator;
        this.useSupervisedDiscretization = useSupervisedDiscretization;
    }

    @Override
    public Classifier setUpClassifier() {
        NaiveBayes network = new NaiveBayes();
        network.setUseKernelEstimator(useKernelEstimator);
        network.setUseSupervisedDiscretization(useSupervisedDiscretization);
        return network;
    }

}
