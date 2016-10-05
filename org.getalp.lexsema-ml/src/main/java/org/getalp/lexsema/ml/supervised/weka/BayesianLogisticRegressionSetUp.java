/**
 *
 */
package org.getalp.lexsema.ml.supervised.weka;

import weka.classifiers.bayes.BayesianLogisticRegression;

/**
 * @author tchechem
 */
public class BayesianLogisticRegressionSetUp implements WekaClassifierSetUp {

    /* (non-Javadoc)
     * @see org.getalp.lexsema.ml.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */
    @Override
    public weka.classifiers.Classifier setUpClassifier() {
        BayesianLogisticRegression classifier = new BayesianLogisticRegression();
        classifier.setNormalizeData(true);
        return classifier;
    }

}
