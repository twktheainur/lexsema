package org.getalp.lexsema.similarity;

import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class TextImpl extends DocumentImpl implements Text {

    private final Collection<Sentence> sentences;

    public TextImpl() {
        sentences = new ArrayList<>();
    }

    public TextImpl(Language language) {
        super(language);
        sentences = new ArrayList<>();
    }


    @Override
    public int numberOfSentences() {
        return sentences.size();
    }

    @Override
    public Iterable<Sentence> sentences() {
        return Collections.unmodifiableCollection(sentences);
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
