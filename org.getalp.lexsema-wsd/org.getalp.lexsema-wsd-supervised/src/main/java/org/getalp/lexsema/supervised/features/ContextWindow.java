package org.getalp.lexsema.supervised.features;

/**
 * Created by tchechem on 11/5/14.
 */
public class ContextWindow {
    private int min;
    private int max;

    public ContextWindow(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static ContextWindow create(int min, int max) {
        return new ContextWindow(min, max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
