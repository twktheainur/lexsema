package org.getalp.lexsema.similarity.measures.tverski;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.wcohen.ss.AbstractStringDistance;
import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.ml.matrix.score.generator.IndexedOverlapScoreMatrixGenerator;
import org.getalp.lexsema.ml.matrix.score.generator.OverlapScoreMatrixGenerator;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.ml.matrix.filters.Filter;
import org.getalp.lexsema.ml.matrix.score.MatrixScorer;
import org.getalp.lexsema.ml.matrix.score.generator.DenseScoreMatrixGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ClassWithTooManyFields")
public class TverskiIndexSimilarityMeasureMatrixImpl implements TverskiIndexSimilarityMeasure {


    private final AbstractStringDistance distance;
    private double alpha = DEFAULT_ALPHA;
    private double beta = DEFAULT_BETA_GAMMA;
    private double gamma = DEFAULT_BETA_GAMMA;
    private boolean computeRatio = true;
    private boolean fuzzyMatching = true;
    private boolean isDistance;
    private MatrixScorer matrixScorer;
    private final List<Filter> filters;


    public TverskiIndexSimilarityMeasureMatrixImpl() {
        distance = new ScaledLevenstein();
        filters = new ArrayList<>();
    }

    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB) {
        return compute(sigA,sigB,null,null);
    }


    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB,
                          Map<String, SemanticSignature> relatedSignaturesA,
                          Map<String, SemanticSignature> relatedSignaturesB) {


        List<String> a = sigA.getStringSymbols();
        List<String> b = sigB.getStringSymbols();

        /*Computing overlap between the semantic signatures*/
        double overlap = computeOverlap(a, b);
        return computeTverski(overlap, a.size(), b.size());
    }

    private double computeTverski(double overlap, int sizeA, int sizeB) {
        double diffA;
        double diffB;
        double length = Math.max(sizeA, sizeB);
        /*Tverski computation*/
        diffA = sizeA / length;
        diffB = sizeB / length;
        diffA -= overlap;
        diffB -= overlap;
        diffA *= diffA;
        diffB *= diffB;
        if (computeRatio) {
            return alpha * overlap / (alpha * overlap + diffA * beta + diffB * gamma);
        } else {
            if (isDistance) {
                diffA = -diffA;
                diffB = -diffB;
            }
            return alpha * overlap - diffA * beta - diffB * gamma;
        }
    }

    private double computeOverlap(List<String> la, List<String> lb) {
        DenseScoreMatrixGenerator matrixGenerator;
        if (fuzzyMatching) {
            matrixGenerator = new OverlapScoreMatrixGenerator(distance, la, lb);
        } else {
            matrixGenerator = new OverlapScoreMatrixGenerator(la, lb);
        }
        DoubleMatrix2D matrix = matrixGenerator.generateDenseScoreMatrix();
        for (Filter filter : filters) {
            matrix = filter.apply(matrix);
        }
        matrixScorer.computeScore(matrix);
        return matrixScorer.computeScore(matrix);
    }

    private double computeIndexedOverlap(List<Integer> la, List<Integer> lb) {
        DenseScoreMatrixGenerator matrixGenerator;
        matrixGenerator = new IndexedOverlapScoreMatrixGenerator(la, lb);
        DoubleMatrix2D matrix = matrixGenerator.generateDenseScoreMatrix();
        for (Filter filter : filters) {
            matrix = filter.apply(matrix);
        }
        matrixScorer.computeScore(matrix);
        return matrixScorer.computeScore(matrix);
    }

    @Override
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public void setBeta(double beta) {
        this.beta = beta;
    }

    @Override
    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    @Override
    public void setComputeRatio(boolean computeRatio) {
        this.computeRatio = computeRatio;
    }

    @Override
    public void setFuzzyMatching(boolean fuzzyMatching) {
        this.fuzzyMatching = fuzzyMatching;
    }

    @Override
    public void setIsDistance(boolean isDistance) {
        this.isDistance = isDistance;
    }

    public void setMatrixScorer(MatrixScorer matrixScorer) {
        this.matrixScorer = matrixScorer;
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }
}
