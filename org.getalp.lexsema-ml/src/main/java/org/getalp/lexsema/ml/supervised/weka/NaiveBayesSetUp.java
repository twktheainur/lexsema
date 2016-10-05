/**
 *
 */
package org.getalp.lexsema.ml.supervised.weka;

import weka.classifiers.bayes.NaiveBayes;

/**
 * @author tchechem
 */
public class NaiveBayesSetUp implements WekaClassifierSetUp {

    private boolean useKernelEstimator = true;
    private boolean useSupervisedDiscretization = true;

    /* (non-Javadoc)
     * @see org.getalp.lexsema.ml.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */

    public NaiveBayesSetUp(boolean useKernelEstimator, boolean useSupervisedDiscretization) {

        this.useKernelEstimator = useKernelEstimator;
        this.useSupervisedDiscretization = useSupervisedDiscretization;
    }

    @Override
    public weka.classifiers.Classifier setUpClassifier() {
        NaiveBayes network = new NaiveBayes();
        network.setUseKernelEstimator(useKernelEstimator);
        network.setUseSupervisedDiscretization(useSupervisedDiscretization);
        return network;
    }

}
