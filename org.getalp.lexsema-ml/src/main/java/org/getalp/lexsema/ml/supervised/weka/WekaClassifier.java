package org.getalp.lexsema.ml.supervised.weka;

import org.getalp.lexsema.ml.supervised.ClassificationOutput;
import org.getalp.lexsema.ml.supervised.FeatureIndex;

import java.io.IOException;
import java.util.List;


public interface WekaClassifier extends org.getalp.lexsema.ml.supervised.Classifier {
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
