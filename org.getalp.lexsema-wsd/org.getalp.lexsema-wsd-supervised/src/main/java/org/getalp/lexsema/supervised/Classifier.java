package org.getalp.lexsema.supervised;

import org.getalp.lexsema.supervised.ClassificationOutput;
import org.getalp.lexsema.supervised.weka.FeatureIndex;

import java.io.IOException;
import java.util.List;

/**
 * Created by tchechem on 17/12/14.
 */
public interface Classifier {
    void loadTrainingData(FeatureIndex featureIndex, String file) throws IOException;

    void saveModel();

    void trainClassifier();

    List<ClassificationOutput> classify(FeatureIndex index, List<String> features);

    boolean isClassifierTrained();
}
