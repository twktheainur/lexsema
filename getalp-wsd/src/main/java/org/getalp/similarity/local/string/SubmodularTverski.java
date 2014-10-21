package org.getalp.similarity.local.string;

import cern.colt.matrix.tdouble.algo.SmpDoubleBlas;
import cern.jet.math.tdouble.DoubleFunctions;
import com.wcohen.ss.AbstractStringDistance;
import org.getalp.disambiguation.Sense;
import org.getalp.optimisation.functions.cache.SetFunctionCache;
import org.getalp.optimisation.functions.input.FunctionInput;
import org.getalp.optimisation.functions.setfunctions.input.FuzzyCommonSubsequenceInput;
import org.getalp.optimisation.functions.setfunctions.input.FuzzyOverlapInputSet;
import org.getalp.optimisation.functions.setfunctions.input.SetFunctionInput;
import org.getalp.optimisation.functions.setfunctions.input.ValueListInput;
import org.getalp.optimisation.functions.setfunctions.submodular.Sum;
import org.getalp.optimisation.functions.setfunctions.submodular.extentions.LovaszExtention;
import org.getalp.optimisation.methods.GradientOptimisation;
import org.getalp.similarity.local.SimilarityMeasure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubmodularTverski implements SimilarityMeasure {


    private AbstractStringDistance distance;
    private SmpDoubleBlas bl;
    SetFunctionCache cache;

    private double alpha = 0;
    private double beta = 0;
    private double gamma = 0;
    private final boolean computeRatio;
    private boolean fuzzyMatching;
    private boolean regularizeOverlapInput;
    private boolean optimizeOverlapInput;
    private boolean regularizeRelations;
    private boolean optimizeRelations;
    private boolean quadraticMatching;
    private boolean extendedLesk;

    private boolean randomInit;
    private double initialLearningRate;


    public SubmodularTverski(AbstractStringDistance distance, boolean computeRatio, double alpha, double beta, double gamma, boolean fuzzyMatching, boolean quadraticMatching, boolean extendedLesk, boolean randomInit, double initialLearningRate, boolean regularizeOverlapInput, boolean optimizeOverlapInput, boolean regularizeRelations, boolean optimizeRelations) {
        this.distance = distance;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.randomInit = randomInit;
        this.initialLearningRate = initialLearningRate;
        this.computeRatio = computeRatio;
        this.fuzzyMatching = fuzzyMatching;
        this.regularizeOverlapInput = regularizeOverlapInput;
        this.optimizeOverlapInput = optimizeOverlapInput;
        this.regularizeRelations = regularizeRelations;
        this.optimizeRelations = optimizeRelations;
        this.quadraticMatching = quadraticMatching;
        this.extendedLesk = extendedLesk;
        bl = new SmpDoubleBlas();
        cache = new SetFunctionCache();
    }

    private double compute(List<String> a, List<String> b, Map<String, List<String>> relatedA, Map<String, List<String>> relatedB) {
        /*Computing overlap between the semantic signatures*/
        double overlap = computeOverlap(a, b);

        /*Creating the similarity values for relation pairs*/
        List<Double> values = new ArrayList<Double>();
        /*This case if for a similarity between two senses that have related senses*/
        if (extendedLesk && relatedA != null && relatedB != null) {
            for (String rA : relatedA.keySet()) {
                for (String rB : relatedA.keySet()) {
                    values.add(computeOverlap(relatedA.get(rA), relatedB.get(rB)));
                }
            }
        /*This case corresponds to a the overlap between a sense' related senses semantic
        signatures and an arbitrary string of text*/
        } else if (extendedLesk && (relatedA == null ^ relatedB == null)) {
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
                values.add(computeOverlap(relatedA.get(r), other));
            }
        }

        SetFunctionInput input = new ValueListInput(values, false);

        /*Declaring sum set function (it's a submodular function)*/
        Sum s = new Sum(1);
        if (regularizeRelations) {
            /*If requested compute the Lovasz Extention (a.k.a. Choquet Integral)*/
            LovaszExtention l = new LovaszExtention();
            s.setExtension(l);
            if (optimizeRelations) {
                /*If requested find the subset that minimises the Lovasz extension value*/
                FunctionInput optimal = l.optimise(new GradientOptimisation(), input);
                input.setInput(optimal.getInput());
            }
            overlap += l.compute(input);
        } else {
            /*If no regularization is required just compute the sum...*/
            overlap += s.F(input);
        }

        /*Tverski computation*/
        double diffA = a.size() - overlap;
        double diffB = b.size() - overlap;
        if (computeRatio) {
            return alpha * overlap / (alpha * overlap + diffA * beta + diffB * gamma);
        } else {
            return alpha * overlap + diffA * beta + diffB * gamma;
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
            input = new FuzzyCommonSubsequenceInput(la, lb, distance, fuzzyMatching);
        } else {
            input = new FuzzyOverlapInputSet(la, lb, null, null, distance, true);
        }

        if (randomInit) {
            input.setInput(input.getInput().assign(DoubleFunctions.random()));
        }

        double returnVal;

        Sum s = new Sum(1);
        if (regularizeOverlapInput) {
            LovaszExtention l = new LovaszExtention();
            s.setExtension(l);
            if (optimizeOverlapInput) {
                FunctionInput optimal = l.optimise(new GradientOptimisation(), input);
                input.setInput(optimal.getInput());
            }
            returnVal = l.compute(input);
        } else {
            returnVal = s.F(input);
        }
        returnVal *= la.size() * lb.size();

        //s.clearCache();

        //double funcVal = la.size()*lb.size()*s.F(input);
        //input.getValues().assign(DoubleFunctions.mult(-1));
        //input.getInput().assign(DoubleFunctions.mult(-1));
        //System.err.println("\nSum="+funcVal);
        //FunctionInput optimal = l.optimise(new GradientOptimisation(), input);

        //FuzzyOverlapInputSet opt = (FuzzyOverlapInputSet)input.copy();
        //opt.setInput(optimal.getInput());
        //double a = s.F(opt);
        //double a = lovaszVal;
        //System.err.println("B="+a+"\n" +
        //        "****************************");
        return returnVal;
    }
}
