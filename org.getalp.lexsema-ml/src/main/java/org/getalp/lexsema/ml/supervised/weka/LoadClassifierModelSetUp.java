/**
 *
 */
package org.getalp.lexsema.ml.supervised.weka;

/**
 * @author tchechem
 */
public class LoadClassifierModelSetUp implements WekaClassifierSetUp {

    String modelFile;

    public LoadClassifierModelSetUp(String modelFile) {
        this.modelFile = modelFile;
    }

    /* (non-Javadoc)
     * @see org.getalp.lexsema.ml.supervised.weka.WekaClassifierSetUp#setUpClassifier()
     */
    @Override
    public weka.classifiers.Classifier setUpClassifier() throws ClassifierSetUpException {
        try {
            return (weka.classifiers.Classifier) weka.core.SerializationHelper.read(modelFile);
        } catch (Exception ignored) {
            throw new ClassifierSetUpException();
        }
    }

}
