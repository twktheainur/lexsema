package org.getalp.io.sentences;

import org.getalp.io.Loader;
import org.getalp.io.Sentence;

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
