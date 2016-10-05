package org.getalp.lexsema.axalign.closure.similarity;

import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.util.URIUtils;
import org.getalp.lexsema.util.caching.Cache;
import org.getalp.lexsema.util.caching.CachePool;
import org.getalp.lexsema.ml.matrix.filters.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class PairwiseSimilarityMatrixGeneratorSim implements PairwiseSimilarityMatrixGenerator {

    private static final double PERCENT_MAX = 100d;

    private static Logger logger = LoggerFactory.getLogger(PairwiseSimilarityMatrixGenerator.class);

    private DoubleMatrix2D similarityMatrix;

    private Collection<Sense> closureSet;
    private SimilarityMeasure crossLingualSimilarity;

    private Cache cache = CachePool.getResource();

    String prefix;

    public PairwiseSimilarityMatrixGeneratorSim(SimilarityMeasure crossLingualSimilarity, Set<Sense> closureSet, String prefix) {
        this.crossLingualSimilarity = crossLingualSimilarity;
        this.closureSet = Collections.unmodifiableSet(closureSet);
        similarityMatrix = DoubleFactory2D.dense.make(closureSet.size(), closureSet.size(), -1d);
        this.prefix = prefix;
    }

    public PairwiseSimilarityMatrixGeneratorSim(SimilarityMeasure crossLingualSimilarity, Set<Sense> closureSet) {
        this(crossLingualSimilarity,closureSet,"");
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
                String key = generateKey(a,b);
                double value;
                //if(cache.exists(key)){
                //    value = Double.valueOf(cache.get(key));
                //} else {
                    value = a.computeSimilarityWith(crossLingualSimilarity, b);
                  //  cache.set(key,String.format("%f",value));
                //}
                similarityMatrix.setQuick(indexA, indexB, value);
                printSimilarityOutput(a, b, value, totalPairs, currentPairIndex);
                indexB++;
                currentPairIndex++;
            }
            indexA++;
            indexB = 0;
        }

    }

    private String generateKey(Sense s1, Sense s2){
        return String.format("%s%s|%s",prefix ,s1.getId(), s2.getId());
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
