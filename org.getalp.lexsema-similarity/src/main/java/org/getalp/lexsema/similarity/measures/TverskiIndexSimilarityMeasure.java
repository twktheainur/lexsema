package org.getalp.lexsema.similarity.measures;

@SuppressWarnings("BooleanParameter")
public interface TverskiIndexSimilarityMeasure extends SimilarityMeasure {
    double DEFAULT_BETA_GAMMA = 0.5d;
    double DEFAULT_ALPHA = 1d;

    void setAlpha(double alpha);

    void setBeta(double beta);

    void setGamma(double gamma);

    void setComputeRatio(boolean computeRatio);

    void setFuzzyMatching(boolean fuzzyMatching);

    void setIsDistance(boolean isDistance);
}
