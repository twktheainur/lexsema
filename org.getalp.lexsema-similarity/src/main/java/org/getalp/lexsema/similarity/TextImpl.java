package org.getalp.lexsema.similarity;

import java.util.ArrayList;
import java.util.Collection;

public class TextImpl extends DocumentImpl implements Text {

    private Collection<Sentence> sentences;

    public TextImpl() {
        sentences = new ArrayList<>();
    }


    @Override
    public int numberOfSentences() {
        return sentences.size();
    }

    @Override
    public boolean isEmpty() {
        return sentences.isEmpty();
    }

    @Override
    public void addSentence(Sentence sentence) {
        addWords(sentence);
        sentences.add(sentence);
    }

}
