package org.getalp.lexsema.supervised.entrydisambiguator;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.ClassificationOutput;
import org.getalp.lexsema.supervised.Classifier;
import org.getalp.lexsema.supervised.EchoClassifier;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.weka.FeatureIndex;
import org.getalp.lexsema.wsd.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EchoLexicalEntryDisambiguator extends SupervisedSequentialLexicalEntryDisambiguator {
    private String dataPath;
    private FeatureIndex featureIndex;

    public EchoLexicalEntryDisambiguator(Configuration c, Document d, int start, int end, int currentIndex, String dataPath, LocalTextFeatureExtractor featureExtractor) {
        super(c, d, start, end, currentIndex, featureExtractor);
        this.dataPath = dataPath;
        featureIndex = new FeatureIndex();
    }

    protected final List<ClassificationOutput> runClassifier(String lemma, List<String> instance) {

        File dataFile = new File(String.format("%s%c%s.csv", dataPath, File.separatorChar, lemma.toLowerCase()));
        if (dataFile.exists()) {
            Classifier classifier;
            classifier = new EchoClassifier();
            try {
                classifier.loadTrainingData(featureIndex, dataPath + File.separatorChar + lemma + ".csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
            classifier.trainClassifier();

            return classifier.classify(featureIndex, instance);
        }
        return new ArrayList<>();
    }
}
