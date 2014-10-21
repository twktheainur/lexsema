package org.getalp.disambiguation.method.sequencial.parameters;


public class WindowedLeskParameters {
    private boolean fallbackFS;
    private boolean minimize;
    private double deltaThreshold;

    public WindowedLeskParameters(boolean fallbackFS, boolean minimize) {
        this.fallbackFS = fallbackFS;
        this.minimize = minimize;
        deltaThreshold = 0.0001d;
    }

    public boolean isFallbackFS() {
        return fallbackFS;
    }

    public boolean isMinimize() {
        return minimize;
    }

    public double getDeltaThreshold() {
        return deltaThreshold;
    }
}
