/**
 *
 */
package org.getalp.lexsema.ml.supervised.weka;

import weka.classifiers.trees.BFTree;

/**
 * @author tchechem
 */
public class BFTreeSetUp implements WekaClassifierSetUp {

    /* (non-Javadoc)
     * @see org.getalp.lexsema.ml.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */
    @Override
    public weka.classifiers.Classifier setUpClassifier() {
        BFTree classifier = new BFTree();
        classifier.setUseGini(true);
        return classifier;
    }

}
