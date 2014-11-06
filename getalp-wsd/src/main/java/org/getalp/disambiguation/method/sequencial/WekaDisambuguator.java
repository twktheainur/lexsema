package org.getalp.disambiguation.method.sequencial;

import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.features.extractors.LocalTextFeatureExtractor;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.WekaLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.parameters.WindowedLeskParameters;
import org.getalp.disambiguation.method.weka.WekaClassifier;
import org.getalp.disambiguation.method.weka.WekaClassifierSetUp;
import org.getalp.io.Document;

import java.util.HashMap;
import java.util.Map;

public class WekaDisambuguator extends SequentialDisambiguator {

    WindowedLeskParameters params;
    private String dataPath;
    private Map<String, WekaClassifier> classifiers;
    private WekaClassifierSetUp classifierSetUp;
    private LocalTextFeatureExtractor featureExtractor;

    public WekaDisambuguator(String dataPath, WekaClassifierSetUp classifierSetUp, LocalTextFeatureExtractor featureExtractor, int numThreads) {
        super(1, numThreads);
        this.dataPath = dataPath;
        classifiers = new HashMap<>();
        this.classifierSetUp = classifierSetUp;
        this.featureExtractor = featureExtractor;
    }

    @Override
    protected SequentialLexicalEntryDisambiguator getEntryDisambiguator(int start, int end, int currentIndex, Configuration c, Document d) {
        return new WekaLexicalEntryDisambiguator(c, d, start, end, currentIndex, dataPath, classifierSetUp, classifiers, featureExtractor);
    }
}
