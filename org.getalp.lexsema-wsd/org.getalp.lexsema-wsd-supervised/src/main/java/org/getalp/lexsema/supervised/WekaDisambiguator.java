package org.getalp.lexsema.supervised;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.entrydisambiguator.WekaLexicalEntryDisambiguator;
import org.getalp.lexsema.supervised.features.TrainingDataExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.ml.supervised.weka.WekaClassifier;
import org.getalp.lexsema.ml.supervised.weka.WekaClassifierSetUp;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.sequencial.SequentialDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.parameters.WindowedLeskParameters;

import java.util.HashMap;
import java.util.Map;

public class WekaDisambiguator extends SequentialDisambiguator {

    WindowedLeskParameters params;
    private String dataPath;
    private Map<String, WekaClassifier> classifiers;
    private WekaClassifierSetUp classifierSetUp;
    private LocalTextFeatureExtractor featureExtractor;
    private TrainingDataExtractor trainingDataExtractor;

    public WekaDisambiguator(String dataPath, WekaClassifierSetUp classifierSetUp, LocalTextFeatureExtractor featureExtractor, int numThreads, TrainingDataExtractor trainingDataExtractor) {
        super(1, numThreads);
        this.dataPath = dataPath;
        classifiers = new HashMap<>();
        this.classifierSetUp = classifierSetUp;
        this.featureExtractor = featureExtractor;
        this.trainingDataExtractor = trainingDataExtractor;
    }

    @Override
    protected SequentialLexicalEntryDisambiguator getEntryDisambiguator(int start, int end, int currentIndex, Configuration c, Document d) {
        return new WekaLexicalEntryDisambiguator(c, d, start, end, currentIndex, dataPath, classifierSetUp, classifiers, featureExtractor, trainingDataExtractor);
    }
}
