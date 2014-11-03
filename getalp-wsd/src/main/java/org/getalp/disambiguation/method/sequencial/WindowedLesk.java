package org.getalp.disambiguation.method.sequencial;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.WindowedLeskLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.parameters.WindowedLeskParameters;
import org.getalp.similarity.semantic.SimilarityMeasure;

public class WindowedLesk extends SequentialDisambiguator {

    WindowedLeskParameters params;
    SimilarityMeasure similarityMeasure;

    public WindowedLesk(int window, SimilarityMeasure sim, WindowedLeskParameters params, int numThreads) {
        super(window, numThreads);
        this.params = params;
        this.similarityMeasure = sim;
    }

    @Override
    protected SequentialLexicalEntryDisambiguator getEntryDisambiguator(int start, int end, int currentIndex, Configuration c, Document d) {
        return new WindowedLeskLexicalEntryDisambiguator(c, d, similarityMeasure, params, start, end, currentIndex);
    }
}
