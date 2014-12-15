package org.getalp.lexsema.similarity.measures;

import cern.jet.math.tdouble.DoubleFunctions;
import com.wcohen.ss.AbstractStringDistance;
import com.wcohen.ss.ScaledLevenstein;
import org.getalp.lexsema.similarity.input.FuzzyCommonSubsequenceInput;
import org.getalp.lexsema.similarity.input.OverlapInputSet;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.optimization.functions.input.FunctionInput;
import org.getalp.optimization.functions.setfunctions.extentions.Extension;
import org.getalp.optimization.functions.setfunctions.extentions.LovaszExtention;
import org.getalp.optimization.functions.setfunctions.input.SetFunctionInput;
import org.getalp.optimization.functions.setfunctions.input.ValueListInput;
import org.getalp.optimization.functions.setfunctions.submodular.Sum;
import org.getalp.optimization.methods.GradientOptimisation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ClassWithTooManyFields")
public class TverskiIndexSimilarityMeasureImpl implements TverskiIndexSimilarityMeasure {


    private AbstractStringDistance distance;
    private double alpha = DEFAULT_ALPHA;
    private double beta = DEFAULT_BETA_GAMMA;
    private double gamma = DEFAULT_BETA_GAMMA;
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


    public TverskiIndexSimilarityMeasureImpl() {
        distance = new ScaledLevenstein();
    }


    @Override
    public double compute(SemanticSignature sigA, SemanticSignature sigB,
                          Map<String, SemanticSignature> relatedSignaturesA,
                          Map<String, SemanticSignature> relatedSignaturesB) {

        List<String> a = sigA.getSymbols();
        List<String> b = sigB.getSymbols();

 /*Computing overlap between the semantic signatures*/
        double overlap = computeOverlap(a, b);



        /*If extendedLesk is enabled, creating the similarity values for relation pairs*/
        if (extendedLesk) {
            overlap += computeExtension(a, b, relatedSignaturesA, relatedSignaturesB);
        }
        return computeTverski(overlap, a.size(), b.size());
    }

    @SuppressWarnings("FeatureEnvy")
    private double computeExtension(List<String> a, List<String> b,
                                    Map<String, SemanticSignature> relatedSignaturesA,
                                    Map<String, SemanticSignature> relatedSignaturesB) {
        List<Double> values = new ArrayList<>();
            /*This case is for a similarity between two senses that have related senses*/
        if (relatedSignaturesA != null && relatedSignaturesB != null) {
            for (String rA : relatedSignaturesB.keySet()) {
                for (String rB : relatedSignaturesB.keySet()) {
                    values.add(computeOverlap(relatedSignaturesA.get(rA).getSymbols(), relatedSignaturesB.get(rB).getSymbols()));
                }
            }
            /*This case corresponds to the overlap between a sense' related synsets' glosses
             *and an arbitrary string of text */
        } else if (relatedSignaturesA == null ^ relatedSignaturesB == null) {
            Map<String, SemanticSignature> nonNullRelated;
            List<String> other;
            if (relatedSignaturesA == null) {
                nonNullRelated = relatedSignaturesB;
                other = a;
            } else {
                nonNullRelated = relatedSignaturesA;
                other = b;
            }
            for (String r : nonNullRelated.keySet()) {
                values.add(computeOverlap(nonNullRelated.get(r).getSymbols(), other));
            }
        }

        SetFunctionInput input = new ValueListInput(values, false);

            /*Declaring sum set function (it's a submodular function)*/
        Sum s = new Sum(1);
        if (regularizeRelations) {
            /*If requested, compute the Lovasz Extension (a.k.a. Choquet Integral)*/
            Extension l = new LovaszExtention();
            s.setExtension(l);
            if (optimizeRelations) {
                /*If requested, find the subset that minimises the Lovasz extension value*/
                FunctionInput optimal = l.optimize(new GradientOptimisation(), input);
                input.setInput(optimal.getInput());
            }
            return l.compute(input);
        } else {
            /*If no regularization is required just compute the sum...*/
            return s.F(input);
        }
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
            Extension l = new LovaszExtention();
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

    @Override
    public void setDistance(AbstractStringDistance distance) {
        this.distance = distance;
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
    public void setRegularizeOverlapInput(boolean regularizeOverlapInput) {
        this.regularizeOverlapInput = regularizeOverlapInput;
    }

    @Override
    public void setRegularizeRelations(boolean regularizeRelations) {
        this.regularizeRelations = regularizeRelations;
    }

    @Override
    public void setOptimizeOverlapInput(boolean optimizeOverlapInput) {
        this.optimizeOverlapInput = optimizeOverlapInput;
    }

    @Override
    public void setOptimizeRelations(boolean optimizeRelations) {
        this.optimizeRelations = optimizeRelations;
    }

    @Override
    public void setQuadraticMatching(boolean quadraticMatching) {
        this.quadraticMatching = quadraticMatching;
    }

    @Override
    public void setExtendedLesk(boolean extendedLesk) {
        this.extendedLesk = extendedLesk;
    }

    @Override
    public void setRandomInit(boolean randomInit) {
        this.randomInit = randomInit;
    }

    @Override
    public void setIsDistance(boolean isDistance) {
        this.isDistance = isDistance;
    }
}
