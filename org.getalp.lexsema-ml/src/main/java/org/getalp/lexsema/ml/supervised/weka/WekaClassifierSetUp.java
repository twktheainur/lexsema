/**
 *
 */
package org.getalp.lexsema.ml.supervised.weka;


/**
 * @author tchechem
 */
public interface WekaClassifierSetUp {

    public weka.classifiers.Classifier setUpClassifier() throws ClassifierSetUpException;

   // public Classifier setUpClassifier(int sparseSize) throws ClassifierSetUpException;
}
