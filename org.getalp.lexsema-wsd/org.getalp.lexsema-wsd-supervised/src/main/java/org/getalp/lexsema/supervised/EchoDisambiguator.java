package org.getalp.lexsema.supervised;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.entrydisambiguator.EchoLexicalEntryDisambiguator;
import org.getalp.lexsema.supervised.features.TrainingDataExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.sequencial.SequentialDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;

public class EchoDisambiguator extends SequentialDisambiguator {

    private String dataPath;
    private LocalTextFeatureExtractor featureExtractor;
    private TrainingDataExtractor trainingDataExtractor;

    public EchoDisambiguator(String dataPath, LocalTextFeatureExtractor featureExtractor, int numThreads, TrainingDataExtractor trainingDataExtractor) {
        super(1, numThreads);
        this.dataPath = dataPath;
        this.featureExtractor = featureExtractor;
        this.trainingDataExtractor = trainingDataExtractor;
    }

    @Override
    protected SequentialLexicalEntryDisambiguator getEntryDisambiguator(int start, int end, int currentIndex, Configuration c, Document d) {
        return new EchoLexicalEntryDisambiguator(c, d, start, end, currentIndex, dataPath, featureExtractor, trainingDataExtractor);
    }
}
