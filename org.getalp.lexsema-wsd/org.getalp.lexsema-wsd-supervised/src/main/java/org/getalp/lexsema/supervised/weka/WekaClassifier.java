package org.getalp.lexsema.supervised.weka;

import org.getalp.lexsema.supervised.ClassificationOutput;

import java.io.IOException;
import java.util.List;

/**
 * Created by tchechem on 04/08/15.
 */
public interface WekaClassifier extends org.getalp.lexsema.supervised.Classifier {
    @Override
    void loadTrainingData(FeatureIndex featureIndex, List<List<String>> trainingInstances, List<String> attrs) throws IOException;

    @Override
    void saveModel();

    @Override
    void trainClassifier();

    @Override
    List<ClassificationOutput> classify(FeatureIndex index, List<String> features);

    @Override
    boolean isClassifierTrained();
}
