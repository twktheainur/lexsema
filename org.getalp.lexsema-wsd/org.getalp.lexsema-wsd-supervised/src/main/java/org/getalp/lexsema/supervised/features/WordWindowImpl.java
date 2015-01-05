package org.getalp.lexsema.supervised.features;

public class WordWindowImpl implements WordWindow {
    String word;
    Integer start;
    Integer end;

    public WordWindowImpl(String lemma, int start, int end) {
        word = lemma;
        this.start = start;
        this.end = end;
    }

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public void updateWindow(int start, int end) {
        if (getStart() < start) {
            this.start = start;
        }
        if (getEnd() < end) {
            this.end = end;
        }
    }
}
