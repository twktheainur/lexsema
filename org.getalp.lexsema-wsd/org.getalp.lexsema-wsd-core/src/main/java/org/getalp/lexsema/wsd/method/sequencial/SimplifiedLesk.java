package org.getalp.lexsema.wsd.method.sequencial;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.SimplifiedLeskLexicalEntryDisambiguator;
import org.getalp.lexsema.wsd.method.sequencial.parameters.SimplifiedLeskParameters;

public class SimplifiedLesk extends SequentialDisambiguator {

    SimplifiedLeskParameters params;
    SimilarityMeasure similarityMeasure;

    public SimplifiedLesk(int window, SimilarityMeasure sim, SimplifiedLeskParameters params, int numThreads) {
        super(window, numThreads);
        this.params = params;
        similarityMeasure = sim;
    }

    @Override
    protected SequentialLexicalEntryDisambiguator getEntryDisambiguator(int start, int end, int currentIndex, Configuration c, Document d) {
        return new SimplifiedLeskLexicalEntryDisambiguator(c, d, similarityMeasure, params, start, end, currentIndex);
    }
}
