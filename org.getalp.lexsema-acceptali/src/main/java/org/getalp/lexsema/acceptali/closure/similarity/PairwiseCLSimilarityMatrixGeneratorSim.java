package org.getalp.lexsema.acceptali.closure.similarity;

import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.util.URIUtils;
import org.getalp.ml.matrix.filters.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class PairwiseCLSimilarityMatrixGeneratorSim implements PairwiseCrossLingualSimilarityMatrixGenerator {

    private static final double PERCENT_MAX = 100d;

    private static Logger logger = LoggerFactory.getLogger(PairwiseCrossLingualSimilarityMatrixGenerator.class);

    private DoubleMatrix2D similarityMatrix;

    private Collection<Sense> closureSet;
    private SimilarityMeasure crossLingualSimilarity;

    public PairwiseCLSimilarityMatrixGeneratorSim(SimilarityMeasure crossLingualSimilarity, Set<Sense> closureSet) {
        this.crossLingualSimilarity = crossLingualSimilarity;
        this.closureSet = Collections.unmodifiableSet(closureSet);
        similarityMatrix = DoubleFactory2D.dense.make(closureSet.size(), closureSet.size(), -1d);

    }

    private static double percentage(int current, int total) {
        return (double) current / (double) total * PERCENT_MAX;
    }

    @Override
    public void generateMatrix() {
        int totalPairs = closureSet.size() * closureSet.size();
        logger.info(String.format("Computing %d pairwise similarities with: %s", totalPairs, crossLingualSimilarity.toString()));
        int indexA = 0;
        int indexB = 0;
        int currentPairIndex = 0;
        for (Sense a : closureSet) {
            for (Sense b : closureSet) {
                double value = a.computeSimilarityWith(crossLingualSimilarity, b);
                similarityMatrix.setQuick(indexA, indexB, value);

                printSimilarityOutput(a, b, value, totalPairs, currentPairIndex);
                indexB++;
                currentPairIndex++;
            }
            indexA++;
            indexB = 0;
        }

    }

    private void printSimilarityOutput(Sense a, Sense b, double value, int totalPairs, int currentPairIndex) {
        logger.info(String.format("\t[%.2f%%] Similarity (%s, %s) = %.4f",
                percentage(currentPairIndex, totalPairs),
                URIUtils.getCanonicalURI(a.getId()),
                URIUtils.getCanonicalURI(b.getId()), value));
    }

    @Override
    public DoubleMatrix2D getScoreMatrix() {
        return similarityMatrix;
    }

    @Override
    public DoubleMatrix2D getScoreMatrix(Filter filter) {
        DoubleMatrix2D processed = similarityMatrix.copy();
        filter.apply(processed);
        return processed;
    }
}
