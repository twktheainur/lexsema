package org.getalp.lexsema.supervised.entrydisambiguator;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.ClassificationOutput;
import org.getalp.lexsema.supervised.Classifier;
import org.getalp.lexsema.supervised.Echo2Classifier;
import org.getalp.lexsema.supervised.features.TrainingDataExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.weka.FeatureIndex;
import org.getalp.lexsema.supervised.weka.FeatureIndexImpl;
import org.getalp.lexsema.wsd.configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EchoLexicalEntryDisambiguator extends SupervisedSequentialLexicalEntryDisambiguator {
    private String dataPath;
    private FeatureIndex featureIndex;

    public EchoLexicalEntryDisambiguator(Configuration c, Document d, int start, int end, int currentIndex, String dataPath, LocalTextFeatureExtractor featureExtractor, TrainingDataExtractor trainingDataExtractor) {
        super(c, d, start, end, currentIndex, featureExtractor, trainingDataExtractor);
        this.dataPath = dataPath;
        featureIndex = new FeatureIndexImpl();
    }

    protected final List<ClassificationOutput> runClassifier(String lemma, List<String> instance) {

        File dataFile = new File(String.format("%s%c%s.csv", dataPath, File.separatorChar, lemma.toLowerCase()));
        if (dataFile.exists()) {
            Classifier classifier;
            classifier = new Echo2Classifier();//changer ici pour echo/echo2
            try {
                List<List<String>> trainingDataFeatures = getLemmaFeatures(lemma);
                if (trainingDataFeatures != null) {
                    classifier.loadTrainingData(featureIndex, getLemmaFeatures(lemma), null);
                    classifier.trainClassifier();
                    return classifier.classify(featureIndex, instance);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}
