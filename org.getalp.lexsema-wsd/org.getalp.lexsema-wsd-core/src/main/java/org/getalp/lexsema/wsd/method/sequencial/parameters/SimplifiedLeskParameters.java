package org.getalp.lexsema.wsd.method.sequencial.parameters;

public class SimplifiedLeskParameters {
    private boolean onlyUniqueWords = false;
    private boolean includeTarget = false;
    private boolean allowTies = false;
    private boolean fallbackFS = false;
    private boolean minimize = false;
    private double deltaThreshold = 0.0001d;

    public boolean isOnlyUniqueWords() {
        return onlyUniqueWords;
    }

    public SimplifiedLeskParameters setOnlyUniqueWords(boolean onlyUniqueWords) {
        this.onlyUniqueWords = onlyUniqueWords;
        return this;
    }

    public boolean isIncludeTarget() {
        return includeTarget;
    }

    public SimplifiedLeskParameters setIncludeTarget(boolean includeTarget) {
        this.includeTarget = includeTarget;
        return this;
    }

    public boolean isAllowTies() {
        return allowTies;
    }

    public SimplifiedLeskParameters setAllowTies(boolean allowTies) {
        this.allowTies = allowTies;
        return this;
    }

    public boolean isFallbackFS() {
        return fallbackFS;
    }

    public SimplifiedLeskParameters setFallbackFS(boolean fallbackFS) {
        this.fallbackFS = fallbackFS;
        return this;
    }

    public boolean isMinimize() {
        return minimize;
    }

    public SimplifiedLeskParameters setMinimize(boolean minimize) {
        this.minimize = minimize;
        return this;
    }

    public double getDeltaThreshold() {
        return deltaThreshold;
    }

    public SimplifiedLeskParameters setDeltaThreshold(double deltaThreshold) {
        this.deltaThreshold = deltaThreshold;
        return this;
    }
}
