/**
 *
 */
package org.getalp.lexsema.supervised.weka;

import weka.classifiers.Classifier;
import weka.classifiers.meta.MultiClassClassifier;

/**
 * @author tchechem
 */
public class WekaMultiClassClassifierSetUp implements WekaClassifierSetUp {

    WekaClassifierSetUp setup;

    public WekaMultiClassClassifierSetUp(WekaClassifierSetUp setup) {
        this.setup = setup;
    }

    @Override
    public Classifier setUpClassifier() {
        MultiClassClassifier mcc = new MultiClassClassifier();
        try {
            mcc.setClassifier(setup.setUpClassifier());
        } catch (ClassifierSetUpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mcc;
    }

}
