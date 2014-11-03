package org.getalp.similarity.semantic.string;

import cern.colt.matrix.tdouble.algo.SmpDoubleBlas;
import cern.jet.math.tdouble.DoubleFunctions;
import com.wcohen.ss.AbstractStringDistance;
import com.wcohen.ss.ScaledLevenstein;
import org.getalp.disambiguation.Sense;
import org.getalp.optimization.functions.cache.SetFunctionCache;
import org.getalp.optimization.functions.input.FunctionInput;
import org.getalp.optimization.functions.setfunctions.extentions.LovaszExtention;
import org.getalp.optimization.functions.setfunctions.input.SetFunctionInput;
import org.getalp.optimization.functions.setfunctions.input.ValueListInput;
import org.getalp.optimization.functions.setfunctions.submodular.Sum;
import org.getalp.optimization.methods.GradientOptimisation;
import org.getalp.similarity.semantic.SimilarityMeasure;
import org.getalp.similarity.semantic.string.input.FuzzyCommonSubsequenceInput;
import org.getalp.similarity.semantic.string.input.OverlapInputSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubmodularTverski implements SimilarityMeasure {


    SetFunctionCache cache;
    private AbstractStringDistance distance;
    private SmpDoubleBlas bl;
    private double alpha = 1d;
    private double beta = 0.5d;
    private double gamma = 0.5d;
    private boolean computeRatio = true;
    private boolean fuzzyMatching = true;
    private boolean regularizeOverlapInput = false;
    private boolean optimizeOverlapInput = false;
    private boolean regularizeRelations = false;
    private boolean optimizeRelations = false;
    private boolean quadraticMatching = false;
    private boolean extendedLesk = true;
    private boolean isDistance;

    private boolean randomInit = false;


    public SubmodularTverski() {
        this.distance = new ScaledLevenstein();
        bl = new SmpDoubleBlas();
        cache = new SetFunctionCache();
    }

    private double compute(List<String> a, List<String> b, Map<String, List<String>> relatedA, Map<String, List<String>> relatedB) {
        /*Computing overlap between the semantic signatures*/
        double overlap = computeOverlap(a, b);



        /*If extendedLesk is enabled, creating the similarity values for relation pairs*/
        if (extendedLesk) {
            List<Double> values = new ArrayList<>();
            /*This case is for a similarity between two senses that have related senses*/
            if (relatedA != null && relatedB != null) {
                for (String rA : relatedA.keySet()) {
                    for (String rB : relatedB.keySet()) {
                        values.add(computeOverlap(relatedA.get(rA), relatedB.get(rB)));
                    }
                }
            /*This case corresponds to the overlap between a sense' related synsets' glosses
             *and an arbitrary string of text */
            } else if (relatedA == null ^ relatedB == null) {
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
                    values.add(computeOverlap(nonNullRelated.get(r), other));
                }
            }

            SetFunctionInput input = new ValueListInput(values, false);

            /*Declaring sum set function (it's a submodular function)*/
            Sum s = new Sum(1);
            if (regularizeRelations) {
            /*If requested, compute the Lovasz Extention (a.k.a. Choquet Integral)*/
                LovaszExtention l = new LovaszExtention();
                s.setExtension(l);
                if (optimizeRelations) {
                /*If requested, find the subset that minimises the Lovasz extension value*/
                    FunctionInput optimal = l.optimize(new GradientOptimisation(), input);
                    input.setInput(optimal.getInput());
                }
                overlap += l.compute(input);
            } else {
            /*If no regularization is required just compute the sum...*/
                overlap += s.F(input);
            }
        }

        double diffA = 0d;
        double diffB = 0d;
        double length = Math.max(a.size(), b.size());
        /*Tverski computation*/
        diffA = a.size() / length;
        diffB = b.size() / length;
        diffA = diffA - overlap;
        diffB = diffB - overlap;
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

    private double computeOverlap(List<String> la, List<String> lb) {

        SetFunctionInput input;
        if (quadraticMatching) {
            input = new FuzzyCommonSubsequenceInput(la, lb,
                    distance, DoubleFunctions.pow(2),
                    fuzzyMatching, isDistance);
        } else {
            if (fuzzyMatching) {
                input = new OverlapInputSet(la, lb, null, null, distance, isDistance);
            } else {
                input = new OverlapInputSet(la, lb, null, null, null, isDistance);
            }
        }

        /*Assign random weights to input pairs, this is useful if we want to start from
        * a random subset when trying to minimize the lovasz extention*/
        if (randomInit) {
            input.setInput(input.getInput().assign(DoubleFunctions.random()));
        }

        double returnVal;
        Sum s = new Sum(1);
        if (regularizeOverlapInput) {
            LovaszExtention l = new LovaszExtention();
            s.setExtension(l);
            if (optimizeOverlapInput) {
                FunctionInput optimal = l.optimize(new GradientOptimisation(), input);
                input.setInput(optimal.getInput());
            }
            returnVal = l.compute(input);
        } else {
            returnVal = s.F(input);
        }
        //returnVal *= (double)Math.min(lb.size(),la.size())/(double)Math.max(lb.size(),la.size());
        returnVal /= input.getValues().size();
        return returnVal;
    }

    public void setDistance(AbstractStringDistance distance) {
        this.distance = distance;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public void setComputeRatio(boolean computeRatio) {
        this.computeRatio = computeRatio;
    }

    public void setFuzzyMatching(boolean fuzzyMatching) {
        this.fuzzyMatching = fuzzyMatching;
    }

    public void setRegularizeOverlapInput(boolean regularizeOverlapInput) {
        this.regularizeOverlapInput = regularizeOverlapInput;
    }

    public void setRegularizeRelations(boolean regularizeRelations) {
        this.regularizeRelations = regularizeRelations;
    }

    public void setOptimizeOverlapInput(boolean optimizeOverlapInput) {
        this.optimizeOverlapInput = optimizeOverlapInput;
    }

    public void setOptimizeRelations(boolean optimizeRelations) {
        this.optimizeRelations = optimizeRelations;
    }

    public void setQuadraticMatching(boolean quadraticMatching) {
        this.quadraticMatching = quadraticMatching;
    }

    public void setExtendedLesk(boolean extendedLesk) {
        this.extendedLesk = extendedLesk;
    }

    public void setRandomInit(boolean randomInit) {
        this.randomInit = randomInit;
    }

    public void setIsDistance(boolean isDistance) {
        this.isDistance = isDistance;
    }
}
