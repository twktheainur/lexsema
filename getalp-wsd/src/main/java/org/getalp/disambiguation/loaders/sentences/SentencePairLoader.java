package org.getalp.disambiguation.loaders.sentences;

import org.getalp.disambiguation.Sentence;
import org.getalp.disambiguation.loaders.Loader;

import java.util.ArrayList;
import java.util.List;

public abstract class SentencePairLoader implements Loader {
    List<List<Sentence>> sentences;

    protected SentencePairLoader() {
        sentences = new ArrayList<>();
    }

    public List<List<Sentence>> getSentencePairs() {
        return sentences;
    }
}
