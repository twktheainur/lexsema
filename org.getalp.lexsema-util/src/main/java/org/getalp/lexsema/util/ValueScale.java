package org.getalp.lexsema.util;

public final class ValueScale {
    public static double scaleValue(double min, double max, double newMin, double newMax, double value) {
        return ((newMax - newMin) * (value - min)) / (max - min) + newMin;
    }
}
