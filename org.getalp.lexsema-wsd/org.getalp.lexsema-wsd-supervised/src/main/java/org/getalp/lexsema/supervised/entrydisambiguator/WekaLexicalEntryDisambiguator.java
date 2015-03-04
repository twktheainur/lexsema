package org.getalp.lexsema.supervised.entrydisambiguator;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.ClassificationOutput;
import org.getalp.lexsema.supervised.features.TrainingDataExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.weka.FeatureIndex;
import org.getalp.lexsema.supervised.weka.WekaClassifier;
import org.getalp.lexsema.supervised.weka.WekaClassifierSetUp;
import org.getalp.lexsema.wsd.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WekaLexicalEntryDisambiguator extends SupervisedSequentialLexicalEntryDisambiguator {
    private String dataPath;
    private Map<String, WekaClassifier> classifiers;
    private FeatureIndex featureIndex;
    private WekaClassifierSetUp classifierSetUp;
    private TrainingDataExtractor trainingDataExtractor;

    public WekaLexicalEntryDisambiguator(Configuration c, Document d, int start, int end, int currentIndex, String dataPath, WekaClassifierSetUp classifierSetUp, Map<String, WekaClassifier> classifiers, LocalTextFeatureExtractor featureExtractor, TrainingDataExtractor trainingDataExtractor) {
        super(c, d, start, end, currentIndex, featureExtractor, trainingDataExtractor);
        this.dataPath = dataPath;
        this.classifiers = classifiers;
        featureIndex = new FeatureIndex();
        this.classifierSetUp = classifierSetUp;
        this.trainingDataExtractor = trainingDataExtractor;
    }

    protected final List<ClassificationOutput> runClassifier(String lemma, List<String> instance) {
        WekaClassifier classifier;
        boolean trainingSuccessful = false;
        if (!classifiers.containsKey(lemma)) {
            classifier = new WekaClassifier(classifierSetUp, dataPath + File.separatorChar + "models" + File.separatorChar + lemma + ".model", false);
            if (!classifier.isClassifierTrained()) {
                try {
                    List<List<String>> trainingInstances = trainingDataExtractor.getWordFeaturesInstances(lemma);
                    if (trainingInstances != null) {
                        classifier.loadTrainingData(featureIndex, trainingInstances, trainingDataExtractor.getAttributes(lemma));
                        classifier.trainClassifier();
                        trainingSuccessful = true;
                    }
                } catch (IOException ignored) {
                    //e.printStackTrace();
                    //System.exit(0);
                    //return new ArrayList<>();
                }

            }
        } else {
            classifier = classifiers.get(lemma);
        }
        if (classifier != null && trainingSuccessful && classifier.isClassifierTrained()) {
            return classifier.classify(featureIndex, instance);
        } else {
            return new ArrayList<>();
        }

    }
}
