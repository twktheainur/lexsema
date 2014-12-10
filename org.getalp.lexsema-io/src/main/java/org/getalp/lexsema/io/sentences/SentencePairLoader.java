package org.getalp.lexsema.io.sentences;


import org.getalp.lexsema.similarity.Sentence;

import java.util.ArrayList;
import java.util.List;

public abstract class SentencePairLoader {
    List<List<Sentence>> sentences;

    protected SentencePairLoader() {
        sentences = new ArrayList<>();
    }

    public List<List<Sentence>> getSentencePairs() {
        return sentences;
    }
}
