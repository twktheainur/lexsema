package org.getalp.lexsema.supervised.entrydisambiguator;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.ClassificationOutput;
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

    public WekaLexicalEntryDisambiguator(Configuration c, Document d, int start, int end, int currentIndex, String dataPath, WekaClassifierSetUp classifierSetUp, Map<String, WekaClassifier> classifiers, LocalTextFeatureExtractor featureExtractor) {
        super(c, d, start, end, currentIndex, featureExtractor);
        this.dataPath = dataPath;
        this.classifiers = classifiers;
        featureIndex = new FeatureIndex();
        this.classifierSetUp = classifierSetUp;
    }

    protected final List<ClassificationOutput> runClassifier(String lemma, List<String> instance) {
        WekaClassifier classifier;
        File dataFile = new File(String.format("%s%c%s.csv", dataPath, File.separatorChar, lemma));
        if (dataFile.exists()) {
            if (!classifiers.containsKey(lemma)) {
                classifier = new WekaClassifier(classifierSetUp, dataPath + File.separatorChar + "models" + File.separatorChar + lemma + ".model", false);
                if (!classifier.isClassifierTrained()) {
                    try {
                        classifier.loadTrainingData(featureIndex, dataPath + File.separatorChar + lemma + ".csv");
                        classifier.trainClassifier();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(0);
                        //return new ArrayList<>();
                    }

                }
            } else {
                classifier = classifiers.get(lemma);
            }
            return classifier.classify(featureIndex, instance);
        }
        return new ArrayList<>();
    }
}
