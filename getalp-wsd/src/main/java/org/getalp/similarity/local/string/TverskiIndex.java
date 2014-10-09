package org.getalp.similarity.local.string;

import com.wcohen.ss.AbstractStringDistance;
import org.getalp.encoding.CodePointWrapper;
import org.getalp.segmentation.Segmenter;
import org.getalp.segmentation.SpaceSegmenter;
import org.getalp.similarity.local.SimilarityMeasure;
import org.getalp.disambiguation.Sense;

import java.util.List;

public class TverskiIndex implements SimilarityMeasure {
    private Segmenter segmenter;

    private double alpha;
    private double beta;
    private boolean fuzzyMatching;
    private boolean symmetric = false;
    private AbstractStringDistance distance;
    private boolean lcss;
    private boolean lcssConstraint = true;


    public TverskiIndex(double alpha, double beta) {
        segmenter = new SpaceSegmenter();
        lcss = false;
        this.alpha = alpha;
        this.beta = beta;
        fuzzyMatching = false;
    }

    public TverskiIndex(double alpha, double beta, boolean fuzzyMatching, boolean symmetric, AbstractStringDistance distance) {
        this.distance = distance;
        lcss = false;
        segmenter = new SpaceSegmenter();
        this.alpha = alpha;
        this.beta = beta;
        this.fuzzyMatching = fuzzyMatching;
        this.symmetric = symmetric;
    }

    public TverskiIndex(double alpha, double beta, boolean fuzzyMatching, boolean symmetric) {
        lcss = true;
        segmenter = new SpaceSegmenter();
        this.alpha = alpha;
        this.beta = beta;
        this.fuzzyMatching = fuzzyMatching;
        this.symmetric = symmetric;
    }

    public TverskiIndex(Segmenter segmenter, double alpha, double beta) {
        this.segmenter = segmenter;
        this.alpha = alpha;
        this.beta = beta;
        fuzzyMatching = false;
    }

    public TverskiIndex(Segmenter segmenter, double alpha, double beta, boolean fuzzyMatching, boolean symmetric) {
        this.segmenter = segmenter;
        this.alpha = alpha;
        this.beta = beta;
        this.fuzzyMatching = fuzzyMatching;
        if (!fuzzyMatching) {
            lcss = false;
        }
        this.symmetric = symmetric;
    }

    public static int longestSubString(String first, String second) {
        if (first == null || second == null || first.length() == 0 || second.length() == 0) {
            return 0;
        }

        int maxLen = 0;
        int fl = first.length();
        int sl = second.length();
        int[][] table = new int[fl][sl];
        CodePointWrapper cpFirst = new CodePointWrapper(first);
        int i = 0;
        for (int cpi : cpFirst) {
            CodePointWrapper cpSecond = new CodePointWrapper(second);
            int j = 0;
            for (int cpj : cpSecond) {
                if (cpi == cpj) {
                    if (i == 0 || j == 0) {
                        table[i][j] = 1;
                    } else {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    }
                    if (table[i][j] > maxLen) {
                        maxLen = table[i][j];
                    }
                }
                j++;
            }
            i++;
        }
        return maxLen;
    }

    private double compute(List<String> a, List<String> b,List<Double> weightsA, List<Double> weightsB) {
        double overlap;
        if (!fuzzyMatching) {
            overlap = computeOverlap(a, b);
        } else {
            overlap = computeFuzzyOverlap(a, b,weightsA,weightsB);
        }
        double diffA = a.size() - overlap;
        double diffB = b.size() - overlap;
        if (symmetric) {
            double factA = Math.min(diffA, diffB);
            double factB = Math.max(diffA, diffB);
            return overlap / (beta * (alpha * factA + (1 - alpha) * factB) + overlap);
        } else {
            return overlap / (overlap + diffA * alpha + diffB * beta);
        }
    }

    public double compute(List<String> a, List<String> b) {
        return compute (a,b,null,null);
    }


    @Override
    public double compute(Sense a, List<String> b) {
        return compute(a.getSignature(),b,a.getWeights(),null);
    }

    @Override
    public double compute(List<String> a, Sense b) {
        return compute(a,b.getSignature(),null,b.getWeights());
    }

    @Override
    public double compute(Sense a, Sense b) {
        return compute(a.getSignature(),b.getSignature(),a.getWeights(),b.getWeights());
    }

    private double computeOverlap(List<String> la, List<String> lb) {
        int size = Math.min(la.size(), lb.size());
        double overlap = 0;
        for (String a : la) {
            for (String b : lb) {
                if(a.equals(b)){
                    overlap++;
                }
            }
        }
        return overlap;
    }

    private double computeFuzzyOverlap(List<String> la, List<String> lb, List<Double> weightsA, List<Double> weightsB) {
        double overlap = 0;
        for (int i=0;i<la.size();i++) {
            String a = la.get(i);
            double wa = 1;
            if(weightsA!=null){
                wa = weightsA.get(i);
            }
            for (int j=0;j<lb.size();j++) {
                String b = lb.get(j);
                double wb =1;
                if(weightsB!=null){
                    wb = weightsB.get(j);
                }
                double score = 0;
                double lcss = longestSubString(a, b);
                double md = Math.max(Math.abs(lcss / a.length()), Math.abs(lcss / b.length()));
                if (!this.lcss) {
                    //score = ((wa+wb)/2d)*distance.score(distance.prepare(a), distance.prepare(b));
                    score = ((wa*wb))*distance.score(distance.prepare(a), distance.prepare(b));
                } else {
                    score = md;
                }
                if (score > 0.999 || score < 1.0 && ((lcssConstraint && lcss >= 3) || !lcssConstraint)) {

                    if (!this.lcss) {
                        if (lcssConstraint) {
                            overlap += score + (1 - score) * (md - 0.5);
                        } else {
                            overlap += score;
                        }
                    } else {
                        overlap += md;
                    }
                }
            }
        }

        return overlap;
    }

    public boolean isLcssConstraint() {
        return lcssConstraint;
    }

    public void setLcssConstraint(boolean lcssConstraint) {
        this.lcssConstraint = lcssConstraint;
    }
}
