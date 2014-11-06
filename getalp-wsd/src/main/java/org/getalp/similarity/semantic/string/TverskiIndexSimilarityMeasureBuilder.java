package org.getalp.similarity.semantic.string;

import com.wcohen.ss.AbstractStringDistance;
import com.wcohen.ss.ScaledLevenstein;

public class TverskiIndexSimilarityMeasureBuilder {
    private AbstractStringDistance distance = new ScaledLevenstein();
    private boolean computeRatio = true;
    private double alpha = 1d;
    private double beta = 0.5d;
    private double gamma = 0.5d;
    private boolean fuzzyMatching = true;
    private boolean quadraticMatching = false;
    private boolean extendedLesk = false;
    private boolean randomInit = false;
    private boolean regularizeOverlapInput = false;
    private boolean optimizeOverlapInput = false;
    private boolean regularizeRelations = false;
    private boolean optimizeRelations = false;
    private boolean isDistance = false;

    public TverskiIndexSimilarityMeasureBuilder distance(AbstractStringDistance distance) {
        this.distance = distance;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder computeRatio(boolean computeRatio) {
        this.computeRatio = computeRatio;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder alpha(double alpha) {
        this.alpha = alpha;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder beta(double beta) {
        this.beta = beta;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder gamma(double gamma) {
        this.gamma = gamma;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder fuzzyMatching(boolean fuzzyMatching) {
        this.fuzzyMatching = fuzzyMatching;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder quadraticWeighting(boolean quadraticMatching) {
        this.quadraticMatching = quadraticMatching;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder extendedLesk(boolean extendedLesk) {
        this.extendedLesk = extendedLesk;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder randomInit(boolean randomInit) {
        this.randomInit = randomInit;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder regularizeOverlapInput(boolean regularizeOverlapInput) {
        this.regularizeOverlapInput = regularizeOverlapInput;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder optimizeOverlapInput(boolean optimizeOverlapInput) {
        this.optimizeOverlapInput = optimizeOverlapInput;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder regularizeRelations(boolean regularizeRelations) {
        this.regularizeRelations = regularizeRelations;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder optimizeRelations(boolean optimizeRelations) {
        this.optimizeRelations = optimizeRelations;
        return this;
    }

    public TverskiIndexSimilarityMeasureBuilder isDistance(boolean isDistance) {
        this.isDistance = isDistance;
        return this;
    }

    public TverskiIndexSimilarityMeasure build() {
        TverskiIndexSimilarityMeasure st = new TverskiIndexSimilarityMeasure();
        st.setAlpha(alpha);
        st.setBeta(beta);
        st.setGamma(gamma);
        st.setOptimizeOverlapInput(optimizeOverlapInput);
        st.setOptimizeRelations(optimizeRelations);
        st.setQuadraticMatching(quadraticMatching);
        st.setRandomInit(randomInit);
        st.setRegularizeOverlapInput(regularizeOverlapInput);
        st.setRegularizeRelations(regularizeRelations);
        st.setFuzzyMatching(fuzzyMatching);
        st.setComputeRatio(computeRatio);
        st.setExtendedLesk(extendedLesk);
        st.setIsDistance(isDistance);
        return st;
    }
}