package org.getalp.lexsema.similarity.measures;

import com.wcohen.ss.AbstractStringDistance;
import org.getalp.ml.matrix.filters.Filter;
import org.getalp.ml.matrix.score.MatrixScorer;

import java.util.ArrayList;
import java.util.List;

public class TverskiIndexSimilarityMeasureMatrixImplBuilder {
    List<Filter> filters = new ArrayList<>();
    private AbstractStringDistance distance;
    private double alpha;
    private double beta;
    private double gamma;
    private boolean computeRatio;
    private boolean fuzzyMatching;
    private boolean isDistance;
    private MatrixScorer matrixScorer;

    public TverskiIndexSimilarityMeasureMatrixImplBuilder setDistance(AbstractStringDistance distance) {
        this.distance = distance;
        return this;
    }

    public TverskiIndexSimilarityMeasureMatrixImplBuilder alpha(double alpha) {
        this.alpha = alpha;
        return this;
    }

    public TverskiIndexSimilarityMeasureMatrixImplBuilder beta(double beta) {
        this.beta = beta;
        return this;
    }

    public TverskiIndexSimilarityMeasureMatrixImplBuilder gamma(double gamma) {
        this.gamma = gamma;
        return this;
    }

    public TverskiIndexSimilarityMeasureMatrixImplBuilder computeRatio(boolean computeRatio) {
        this.computeRatio = computeRatio;
        return this;
    }

    public TverskiIndexSimilarityMeasureMatrixImplBuilder fuzzyMatching(boolean fuzzyMatching) {
        this.fuzzyMatching = fuzzyMatching;
        return this;
    }

    public TverskiIndexSimilarityMeasureMatrixImplBuilder isDistance(boolean isDistance) {
        this.isDistance = isDistance;
        return this;
    }

    public TverskiIndexSimilarityMeasureMatrixImplBuilder matrixScorer(MatrixScorer matrixScorer) {
        this.matrixScorer = matrixScorer;
        return this;
    }

    public TverskiIndexSimilarityMeasureMatrixImplBuilder filter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public TverskiIndexSimilarityMeasureMatrixImpl build() {
        TverskiIndexSimilarityMeasureMatrixImpl tverskiIndexSimilarityMeasureMatrix = new TverskiIndexSimilarityMeasureMatrixImpl();
        tverskiIndexSimilarityMeasureMatrix.setAlpha(alpha);
        tverskiIndexSimilarityMeasureMatrix.setBeta(beta);
        tverskiIndexSimilarityMeasureMatrix.setGamma(gamma);
        tverskiIndexSimilarityMeasureMatrix.setFuzzyMatching(fuzzyMatching);
        tverskiIndexSimilarityMeasureMatrix.setComputeRatio(computeRatio);
        tverskiIndexSimilarityMeasureMatrix.setIsDistance(isDistance);
        tverskiIndexSimilarityMeasureMatrix.setMatrixScorer(matrixScorer);
        for (Filter f : filters) {
            tverskiIndexSimilarityMeasureMatrix.addFilter(f);
        }
        return tverskiIndexSimilarityMeasureMatrix;
    }
}