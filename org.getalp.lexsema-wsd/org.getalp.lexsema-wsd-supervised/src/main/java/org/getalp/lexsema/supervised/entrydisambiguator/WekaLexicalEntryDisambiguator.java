package org.getalp.lexsema.supervised.entrydisambiguator;


import org.getalp.lexsema.ml.supervised.ClassificationOutput;
import org.getalp.lexsema.ml.supervised.Classifier;
import org.getalp.lexsema.ml.supervised.FeatureIndex;
import org.getalp.lexsema.ml.supervised.FeatureIndexImpl;
import org.getalp.lexsema.ml.supervised.weka.WekaClassifier;
import org.getalp.lexsema.ml.supervised.weka.WekaClassifierImpl;
import org.getalp.lexsema.ml.supervised.weka.WekaClassifierSetUp;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.features.TrainingDataExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
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

        logger.debug(MessageFormat.format("disambiguation of {0}", lemma));
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

                    logger.debug(MessageFormat.format("TrainingInstances {0}", trainingInstances));

                    if (trainingInstances != null) {

                        /***** ICI ? ******
                        for(List<String> l: trainingInstances){

                            System.out.println("l = "+l);
                        }
                        /***** ICI ? *******/

                        logger.debug(MessageFormat.format("Number of examples :{0}", trainingInstances.size()));
                        if(trainingInstances.size() <= 5000) {
                            performTraining(classifier, trainingInstances, lemma);
                            trainingSuccessful = true;
                        }
                    }
                } catch (IOException e) {
                    logger.error(String.format("Error while classifying the word%s:%s", lemma, e.getLocalizedMessage()));
                    System.exit(0);
                }

            }
        }
        logger.debug(MessageFormat.format("Classifier : {0}", classifier));
        logger.debug(MessageFormat.format("TrainingSuccessful : {0}", trainingSuccessful));
        logger.debug(MessageFormat.format("ClassifierTrained : {0}", classifier.isClassifierTrained()));
        if (classifier != null && trainingSuccessful && classifier.isClassifierTrained()) {
            result = classifyInstance(classifier,instance);
        }
        logger.debug(MessageFormat.format("disambiguation of {0} done", lemma));
        logger.debug(MessageFormat.format("result : {0}", result));
        return result;

    }

    private void performTraining(Classifier classifier, List<List<String>> trainingInstances,String lemma) throws IOException {
        classifier.loadTrainingData(featureIndex, trainingInstances, trainingDataExtractor.getAttributes(lemma));
        classifier.trainClassifier();
    }

    private List<ClassificationOutput> classifyInstance(Classifier classifier, List<String> instance){
        logger.debug(MessageFormat.format("Instances : {0}", instance.size()));
        return classifier.classify(featureIndex, instance);
    }
}
