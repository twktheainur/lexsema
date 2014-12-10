package org.getalp.lexsema.similarity.measures;

import com.wcohen.ss.AbstractStringDistance;


@SuppressWarnings("BooleanParameter")
public interface TverskiIndexSimilarityMeasure extends SimilarityMeasure {
    double DEFAULT_BETA_GAMMA = 0.5d;
    double DEFAULT_ALPHA = 1d;

    void setDistance(AbstractStringDistance distance);

    void setAlpha(double alpha);

    void setBeta(double beta);

    void setGamma(double gamma);

    void setComputeRatio(boolean computeRatio);

    void setFuzzyMatching(boolean fuzzyMatching);

    void setRegularizeOverlapInput(boolean regularizeOverlapInput);

    void setRegularizeRelations(boolean regularizeRelations);

    void setOptimizeOverlapInput(boolean optimizeOverlapInput);

    void setOptimizeRelations(boolean optimizeRelations);

    void setQuadraticMatching(boolean quadraticMatching);

    void setExtendedLesk(boolean extendedLesk);

    void setRandomInit(boolean randomInit);

    void setIsDistance(boolean isDistance);
}
