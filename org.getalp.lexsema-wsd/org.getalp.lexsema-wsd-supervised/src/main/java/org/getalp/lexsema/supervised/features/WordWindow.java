package org.getalp.lexsema.supervised.features;

public interface WordWindow {
    public String getWord();

    public int getStart();

    public int getEnd();

    public void updateWindow(int start, int end);
}
