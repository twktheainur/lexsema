package org.getalp.similarity.local.string;

import com.wcohen.ss.AbstractStringDistance;
import org.getalp.disambiguation.Sense;
import org.getalp.segmentation.Segmenter;
import org.getalp.segmentation.SpaceSegmenter;
import org.getalp.similarity.local.SimilarityMeasure;
import org.getalp.util.SubSequences;

import java.util.List;
import java.util.Map;

public class TverskiIndex implements SimilarityMeasure {
    private Segmenter segmenter;

    private double alpha;
    private double beta;
    private double gamma;
    private boolean ratioComputation;
    private boolean fuzzyMatching;
    private boolean symmetric = false;
    private AbstractStringDistance distance;
    private boolean extendedLesk;
    private boolean quadraticOverlap;

    public TverskiIndex(AbstractStringDistance distance, boolean ratioComputation, double alpha, double beta, double gamma, boolean fuzzyMatching, boolean quadraticOverlap, boolean symmetric, boolean extendedLesk) {
        this.distance = distance;
        segmenter = new SpaceSegmenter();
        this.alpha = alpha;
        this.beta = beta;
        this.fuzzyMatching = fuzzyMatching;
        this.symmetric = symmetric;
        this.ratioComputation = ratioComputation;
        this.extendedLesk = extendedLesk;
        this.quadraticOverlap = quadraticOverlap;
        this.gamma = gamma;

    }

    private double compute(List<String> a, List<String> b, Map<String, List<String>> relatedA, Map<String, List<String>> relatedB) {
        double overlap;

        /*Overlap between the glosses of the two senses*/
        if (quadraticOverlap) {
            overlap = computeQuadraticFuzzyOverlap(a, b);
        } else {
            overlap = computeFuzzyOverlap(a, b);
        }

        /*
         * Extended lesk relation pair computation
         */
        if (extendedLesk && relatedA != null && relatedB != null) {
            for (String rA : relatedA.keySet()) {
                for (String rB : relatedA.keySet()) {
                    overlap += computeQuadraticFuzzyOverlap(relatedA.get(rA), relatedB.get(rB));
                }
            }
        } else if (extendedLesk && relatedA == null ^ relatedB == null) {
            Map<String, List<String>> nonNullRelated;
            List<String> other;
            if (relatedA == null) {
                nonNullRelated = relatedB;
                other = a;
            } else {
                nonNullRelated = relatedA;
                other = b;
            }
            for (String r : nonNullRelated.keySet()) {
                overlap += computeQuadraticFuzzyOverlap(nonNullRelated.get(r), other);
            }
        }

        double diffA = a.size() - overlap;
        double diffB = b.size() - overlap;
        /*Standard lesk*/
        if (!ratioComputation) {
            return overlap;
        }
        /*Tverski index computation*/
        else if (symmetric) {
            double factA = Math.min(diffA, diffB);
            double factB = Math.max(diffA, diffB);
            return alpha * overlap / (gamma * (beta * factA + (1 - beta) * factB) + overlap);
        } else {
            return alpha * overlap / (alpha * overlap + diffA * beta + diffB * gamma);
        }
    }

    @Override
    public double compute(List<String> a, List<String> b) {
        return compute(a, b, null, null);
    }


    @Override
    public double compute(Sense a, List<String> b) {
        return compute(a.getSignature(), b, a.getRelatedSignatures(), null);
    }

    @Override
    public double compute(List<String> a, Sense b) {
        return compute(a, b.getSignature(), null, b.getRelatedSignatures());
    }

    @Override
    public double compute(Sense a, Sense b) {
        return compute(a.getSignature(), b.getSignature(), a.getRelatedSignatures(), b.getRelatedSignatures());
    }

    private double computeFuzzyOverlap(List<String> la, List<String> lb) {
        double overlap = 0;
        for (String a : la) {
            for (String b : lb) {
                if (fuzzyMatching) {
                    overlap += distance.score(distance.prepare(a), distance.prepare(b));
                } else if (a.equals(b)) {
                    overlap++;
                }
            }
        }
        return overlap;
    }

    private double computeQuadraticFuzzyOverlap(List<String> la, List<String> lb) {
        double overlap = 0;
        List<Double> subsequenceLengths;
        if (fuzzyMatching) {
            subsequenceLengths = SubSequences.fuzzyLongestCommonSubSequences(la, lb, distance, 0.5);
        } else {
            subsequenceLengths = SubSequences.longestCommonSubSequences(la, lb);
        }
        for (Double len : subsequenceLengths) {
            overlap += len * len;
        }
        return overlap;
    }
}
