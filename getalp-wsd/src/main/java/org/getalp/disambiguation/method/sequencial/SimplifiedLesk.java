package org.getalp.disambiguation.method.sequencial;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.entrydisambiguators.SimplifiedLeskLexicalEntryDisambiguator;
import org.getalp.disambiguation.method.sequencial.parameters.SimplifiedLeskParameters;
import org.getalp.similarity.semantic.SimilarityMeasure;

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
