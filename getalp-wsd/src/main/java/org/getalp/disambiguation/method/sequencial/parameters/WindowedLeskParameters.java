package org.getalp.disambiguation.method.sequencial.parameters;


public class WindowedLeskParameters {
    private boolean fallbackFS;
    private boolean minimize;
    private double deltaThreshold;

    public WindowedLeskParameters() {
        fallbackFS = false;
        minimize = false;
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

    public WindowedLeskParameters setFallbackFS(boolean fallbackFS) {
        this.fallbackFS = fallbackFS;
        return this;
    }

    public WindowedLeskParameters setMinimize(boolean minimize) {
        this.minimize = minimize;
        return this;
    }

    public WindowedLeskParameters setDeltaThreshold(double deltaThreshold) {
        this.deltaThreshold = deltaThreshold;
        return this;
    }
}
