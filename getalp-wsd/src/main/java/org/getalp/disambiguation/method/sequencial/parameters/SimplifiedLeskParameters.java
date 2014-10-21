package org.getalp.disambiguation.method.sequencial.parameters;

public class SimplifiedLeskParameters {
    private boolean addSenseSignatures;
    private boolean onlyOverlapContexts;
    private boolean onlyUniqueWords;
    private boolean includeTarget;
    private boolean allowTies;
    private boolean fallbackFS;
    private boolean minimize;
    private double deltaThreshold;

    public SimplifiedLeskParameters(boolean addSenseSignatures, boolean onlyOverlapContexts, boolean onlyUniqueWords, boolean includeTarget, boolean allowTies, boolean fallbackFS, boolean minimize) {
        this.addSenseSignatures = addSenseSignatures;
        this.onlyOverlapContexts = onlyOverlapContexts;
        this.onlyUniqueWords = onlyUniqueWords;
        this.includeTarget = includeTarget;
        this.allowTies = allowTies;
        this.fallbackFS = fallbackFS;
        this.minimize = minimize;
        deltaThreshold = 0.0001d;
    }

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
}
