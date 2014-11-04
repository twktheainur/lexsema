package org.getalp.disambiguation;

import java.util.ArrayList;
import java.util.List;

public class Text extends Document {

    private List<Sentence> sentences;

    public Text() {
        sentences = new ArrayList<>();
    }

    public List<Sentence> getSentences() {
        return sentences;
    }
}
