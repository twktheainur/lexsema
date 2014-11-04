package org.getalp.similarity.semantic.string;

import com.wcohen.ss.AbstractStringDistance;

public class TverskiIndexBuilder {
    private AbstractStringDistance distance;
    private boolean ratioComputation;
    private double alpha;
    private double beta;
    private double gamma;
    private boolean fuzzyMatching;
    private boolean quadraticOverlap;
    private boolean symmetric;
    private boolean extendedLesk;

    public TverskiIndexBuilder setDistance(AbstractStringDistance distance) {
        this.distance = distance;
        return this;
    }

    public TverskiIndexBuilder setRatioComputation(boolean ratioComputation) {
        this.ratioComputation = ratioComputation;
        return this;
    }

    public TverskiIndexBuilder setAlpha(double alpha) {
        this.alpha = alpha;
        return this;
    }

    public TverskiIndexBuilder setBeta(double beta) {
        this.beta = beta;
        return this;
    }

    public TverskiIndexBuilder setGamma(double gamma) {
        this.gamma = gamma;
        return this;
    }

    public TverskiIndexBuilder setFuzzyMatching(boolean fuzzyMatching) {
        this.fuzzyMatching = fuzzyMatching;
        return this;
    }

    public TverskiIndexBuilder setQuadraticOverlap(boolean quadraticOverlap) {
        this.quadraticOverlap = quadraticOverlap;
        return this;
    }

    public TverskiIndexBuilder setSymmetric(boolean symmetric) {
        this.symmetric = symmetric;
        return this;
    }

    public TverskiIndexBuilder setExtendedLesk(boolean extendedLesk) {
        this.extendedLesk = extendedLesk;
        return this;
    }

    public TverskiIndex createTverskiIndex() {
        return new TverskiIndex(distance, ratioComputation, alpha, beta, gamma, fuzzyMatching, quadraticOverlap, symmetric, extendedLesk);
    }
}