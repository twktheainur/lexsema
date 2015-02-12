package org.getalp.lexsema.util;

public final class ValueScale {

    private ValueScale() {
    }

    public static int scaleValue(int val, int min, int max, int nmin, int nmax) {
        return (int) Math.round(scaleValue((double) val, (double) min, (double) max, (double) nmin, (double) nmax));
    }

    public static double scaleValue(double val, double min, double max, double nmin, double nmax) {
        return (val * nmax - val * nmin - min * nmax + nmin * max) / (max - min);
    }
}
