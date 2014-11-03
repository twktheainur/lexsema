package org.getalp.disambiguation.method.sequencial.parameters;

public class SimplifiedLeskParameters {
    private boolean addSenseSignatures = false;
    private boolean onlyOverlapContexts = false;
    private boolean onlyUniqueWords = false;
    private boolean includeTarget = false;
    private boolean allowTies = false;
    private boolean fallbackFS = false;
    private boolean minimize = false;
    private double deltaThreshold = 0.0001d;

    public boolean isAddSenseSignatures() {
        return addSenseSignatures;
    }

    public boolean isOnlyOverlapContexts() {
        return onlyOverlapContexts;
    }

    public boolean isOnlyUniqueWords() {
        return onlyUniqueWords;
    }

    public boolean isIncludeTarget() {
        return includeTarget;
    }

    public boolean isAllowTies() {
        return allowTies;
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

    public SimplifiedLeskParameters setAddSenseSignatures(boolean addSenseSignatures) {
        this.addSenseSignatures = addSenseSignatures;
        return this;
    }

    public SimplifiedLeskParameters setOnlyOverlapContexts(boolean onlyOverlapContexts) {
        this.onlyOverlapContexts = onlyOverlapContexts;
        return this;
    }

    public SimplifiedLeskParameters setOnlyUniqueWords(boolean onlyUniqueWords) {
        this.onlyUniqueWords = onlyUniqueWords;
        return this;
    }

    public SimplifiedLeskParameters setIncludeTarget(boolean includeTarget) {
        this.includeTarget = includeTarget;
        return this;
    }

    public SimplifiedLeskParameters setAllowTies(boolean allowTies) {
        this.allowTies = allowTies;
        return this;
    }

    public SimplifiedLeskParameters setFallbackFS(boolean fallbackFS) {
        this.fallbackFS = fallbackFS;
        return this;
    }

    public SimplifiedLeskParameters setMinimize(boolean minimize) {
        this.minimize = minimize;
        return this;
    }

    public SimplifiedLeskParameters setDeltaThreshold(double deltaThreshold) {
        this.deltaThreshold = deltaThreshold;
        return this;
    }
}
