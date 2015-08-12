package org.getalp.lexsema.supervised.features;

/**
 * Created by tchechem on 11/5/14.
 */
public class ContextWindowImpl implements ContextWindow {
    private int min;
    private int max;

    public ContextWindowImpl(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static ContextWindow create(int min, int max) {
        return new ContextWindowImpl(min, max);
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public int getMax() {
        return max;
    }
}
