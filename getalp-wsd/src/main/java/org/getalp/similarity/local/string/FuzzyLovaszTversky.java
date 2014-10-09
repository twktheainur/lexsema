package org.getalp.similarity.local.string;

import cern.colt.matrix.tdouble.algo.SmpDoubleBlas;
import cern.jet.math.tdouble.DoubleFunctions;
import com.wcohen.ss.AbstractStringDistance;
import org.getalp.disambiguation.Sense;
import org.getalp.optimisation.functions.cache.SetFunctionCache;
import org.getalp.optimisation.functions.input.FunctionInput;
import org.getalp.optimisation.functions.setfunctions.Sum;
import org.getalp.optimisation.functions.setfunctions.input.FuzzyOverlapInputSet;
import org.getalp.optimisation.functions.setfunctions.submodular.extentions.LovaszExtention;
import org.getalp.optimisation.methods.GradientOptimisation;
import org.getalp.similarity.local.SimilarityMeasure;

import java.util.List;

public class FuzzyLovaszTversky implements SimilarityMeasure {

    private AbstractStringDistance distance;
    private SmpDoubleBlas bl;
    SetFunctionCache cache;

    private double alpha = 0;
    private double beta = 0;

    private boolean randomInit;
    private double initialLearningRate;


    public FuzzyLovaszTversky(AbstractStringDistance distance, double alpha, double beta, boolean randomInit, double initialLearningRate) {
        this.distance = distance;
        this.alpha = alpha;
        this.beta = beta;
        this.randomInit = randomInit;
        this.initialLearningRate = initialLearningRate;
        bl = new SmpDoubleBlas();
        cache = new SetFunctionCache();
    }

    private double compute(List<String> a, List<String> b, List<Double> weightsA, List<Double> weightsB) {
        double overlap = computeOptimalLovaszFuzzyOverlap(a, b, weightsA, weightsB);
        double diffA = a.size() - overlap;
        double diffB = b.size() - overlap;
        return overlap / (overlap + diffA * alpha + diffB * beta);
    }

    public double compute(List<String> a, List<String> b) {
        return compute(a, b, null, null);
    }


    @Override
    public double compute(Sense a, List<String> b) {
        return compute(a.getSignature(), b, a.getWeights(), null);
    }

    @Override
    public double compute(List<String> a, Sense b) {
        return compute(a, b.getSignature(), null, b.getWeights());
    }

    @Override
    public double compute(Sense a, Sense b) {
        return compute(a.getSignature(), b.getSignature(), a.getWeights(), b.getWeights());
    }

    private double computeOptimalLovaszFuzzyOverlap(List<String> la, List<String> lb, List<Double> weightsA, List<Double> weightsB) {
        FuzzyOverlapInputSet input = new FuzzyOverlapInputSet(la,lb,weightsA,weightsB,distance);

        if(randomInit){
            input.setInput(input.getInput().assign(DoubleFunctions.random()));
        }

        Sum s = new Sum(1);
        LovaszExtention l = new LovaszExtention();
        s.setExtension(l);

        //input.getValues().assign(DoubleFunctions.mult(-1));
        //input.getInput().assign(DoubleFunctions.mult(-1));
        System.err.println("****************************\nA="+s.F(input));
        FunctionInput optimal = l.optimise(new GradientOptimisation(), input);

        FuzzyOverlapInputSet opt = (FuzzyOverlapInputSet)input.copy();
        opt.setInput(optimal.getInput());
        double a = s.F(opt);
        System.err.println("B="+a+"\n" +
                "****************************");
        return a;
    }
}
