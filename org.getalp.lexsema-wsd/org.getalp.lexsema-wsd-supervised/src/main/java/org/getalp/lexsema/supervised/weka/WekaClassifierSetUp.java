/**
 *
 */
package org.getalp.lexsema.supervised.weka;

import weka.classifiers.Classifier;

/**
 * @author tchechem
 */
public interface WekaClassifierSetUp {

    public Classifier setUpClassifier() throws ClassifierSetUpException;

   // public Classifier setUpClassifier(int sparseSize) throws ClassifierSetUpException;
}
