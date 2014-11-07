package org.getalp.lexsema.wsd.method.sequencial;

import org.getalp.lexsema.io.Document;
import org.getalp.lexsema.similarity.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.WindowedLeskLexicalEntryDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.parameters.WindowedLeskParameters;

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
