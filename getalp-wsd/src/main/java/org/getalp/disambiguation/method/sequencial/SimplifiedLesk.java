package org.getalp.disambiguation.method.sequencial;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.SimplifiedLeskLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.parameters.SimplifiedLeskParameters;
import org.getalp.similarity.local.SimilarityMeasure;

public class SimplifiedLesk extends SequentialDisambiguator {

    SimplifiedLeskParameters params;

    public SimplifiedLesk(int window, SimilarityMeasure sim, SimplifiedLeskParameters params, int numThreads) {
        super(window, sim, numThreads);
        this.params = params;
    }

    @Override
    protected SequentialLexicalEntryDisambiguator getEntryDisambiguator(int start, int end, int currentIndex, Configuration c, Document d) {
        return new SimplifiedLeskLexicalEntryDisambiguator(c, d, getSim(), params, start, end, currentIndex);
    }
}
