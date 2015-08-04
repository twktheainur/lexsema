package org.getalp.lexsema.supervised.entrydisambiguator;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.ClassificationOutput;
import org.getalp.lexsema.supervised.Classifier;
import org.getalp.lexsema.supervised.features.TrainingDataExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.weka.*;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class WekaLexicalEntryDisambiguator extends SupervisedSequentialLexicalEntryDisambiguator {
    private final String dataPath;
    private final Map<String, WekaClassifier> classifiers;
    private final FeatureIndex featureIndex;
    private final WekaClassifierSetUp classifierSetUp;
    private final TrainingDataExtractor trainingDataExtractor;

    private static final Logger logger = LoggerFactory.getLogger(WekaLexicalEntryDisambiguator.class);

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public WekaLexicalEntryDisambiguator(Configuration configuration, Document document, int start, int end, int currentIndex, String dataPath, WekaClassifierSetUp classifierSetUp, Map<String, WekaClassifier> classifiers, LocalTextFeatureExtractor featureExtractor, TrainingDataExtractor trainingDataExtractor) {
        super(configuration, document, start, end, currentIndex, featureExtractor, trainingDataExtractor);
        this.dataPath = dataPath;
        this.classifiers = Collections.unmodifiableMap(classifiers);
        featureIndex = new FeatureIndexImpl();
        this.classifierSetUp = classifierSetUp;
        this.trainingDataExtractor = trainingDataExtractor;
    }

    @Override
    protected final List<ClassificationOutput> runClassifier(String lemma, List<String> instance) {
        WekaClassifier classifier;
        List<ClassificationOutput> result = Collections.emptyList();
        boolean trainingSuccessful = false;
        if (classifiers.containsKey(lemma)) {
            classifier = classifiers.get(lemma);
        } else {
            classifier = new WekaClassifierImpl(classifierSetUp, MessageFormat.format("{0}{1}models{2}{3}.model", dataPath, File.separatorChar, File.separatorChar, lemma), false);
            if (!classifier.isClassifierTrained()) {
                try {
                    List<List<String>> trainingInstances = trainingDataExtractor.getWordFeaturesInstances(lemma);
                    if (trainingInstances != null) {
                        performTraining(classifier,trainingInstances,lemma);
                        trainingSuccessful = true;
                    }
                } catch (IOException e) {
                    logger.error(String.format("Error while classifying the word%s:%s", lemma, e.getLocalizedMessage()));
                    System.exit(0);
                }

            }
        }
        if (classifier != null && trainingSuccessful && classifier.isClassifierTrained()) {
            result = classifyInstance(classifier,instance);
        }
        return result;

    }

    private void performTraining(Classifier classifier, List<List<String>> trainingInstances,String lemma) throws IOException {
        classifier.loadTrainingData(featureIndex, trainingInstances, trainingDataExtractor.getAttributes(lemma));
        classifier.trainClassifier();
    }

    private List<ClassificationOutput> classifyInstance(Classifier classifier, List<String> instance){
        return classifier.classify(featureIndex, instance);
    }
}
