/**
 *
 */
package org.getalp.lexsema.supervised.weka;

import weka.classifiers.Classifier;
import weka.classifiers.trees.BFTree;

/**
 * @author tchechem
 */
public class BFTreeSetUp implements WekaClassifierSetUp {

    /* (non-Javadoc)
     * @see org.getalp.lexsema.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */
    @Override
    public Classifier setUpClassifier() {
        BFTree classifier = new BFTree();
        classifier.setUseGini(true);
        return classifier;
    }

}
