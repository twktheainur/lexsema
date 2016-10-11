package org.getalp.lexsema.io.sentences;


import org.getalp.lexsema.similarity.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SentencePairLoader {
    private List<List<Text>> sentences;

    protected SentencePairLoader() {
        sentences = new ArrayList<>();
    }

    public List<List<Text>> getSentencePairs() {
        return Collections.unmodifiableList(sentences);
    }
}
