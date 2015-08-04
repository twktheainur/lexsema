/**
 *
 */
package org.getalp.lexsema.supervised.weka;

import weka.classifiers.Classifier;

/**
 * @author tchechem
 */
public class LoadClassifierModelSetUp implements WekaClassifierSetUp {

    String modelFile;

    public LoadClassifierModelSetUp(String modelFile) {
        this.modelFile = modelFile;
    }

    /* (non-Javadoc)
     * @see org.getalp.lexsema.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */
    @Override
    public Classifier setUpClassifier() throws ClassifierSetUpException {
        try {
            return (Classifier) weka.core.SerializationHelper.read(modelFile);
        } catch (Exception ignored) {
            throw new ClassifierSetUpException();
        }
    }

}
