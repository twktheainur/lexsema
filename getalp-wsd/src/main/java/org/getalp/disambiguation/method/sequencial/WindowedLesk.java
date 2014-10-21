package org.getalp.disambiguation.method.sequencial;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.WindowedLeskLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.parameters.WindowedLeskParameters;
import org.getalp.similarity.local.SimilarityMeasure;

public class WindowedLesk extends SequentialDisambiguator {

    WindowedLeskParameters params;

    public WindowedLesk(int window, SimilarityMeasure sim, WindowedLeskParameters params, int numThreads) {
        super(window, sim, numThreads);
        this.params = params;
    }

    @Override
    protected SequentialLexicalEntryDisambiguator getEntryDisambiguator(int start, int end, int currentIndex, Configuration c, Document d) {
        return new WindowedLeskLexicalEntryDisambiguator(c, d, getSim(), params, start, end, currentIndex);
    }
}
