package org.getalp.lexsema.supervised;

import org.getalp.lexsema.io.Document;
import org.getalp.lexsema.supervised.entrydisambiguator.WekaLexicalEntryDisambiguator;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.weka.WekaClassifier;
import org.getalp.lexsema.supervised.weka.WekaClassifierSetUp;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.sequencial.SequentialDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.parameters.WindowedLeskParameters;

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
