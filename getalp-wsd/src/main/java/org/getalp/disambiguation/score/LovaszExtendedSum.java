package org.getalp.disambiguation.score;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.optimisation.functions.setfunctions.Sum;
import org.getalp.optimisation.functions.setfunctions.submodular.PairwiseSimilarity;
import org.getalp.optimisation.functions.setfunctions.submodular.SubmodularScore;

public class LovaszExtendedSum extends SubmodularScore {

    protected LovaszExtendedSum(Document d) {
        super(d, new Sum(-1));
    }

    @Override
    public double computeBaseSetScore(Configuration c, PairwiseSimilarity input) {
        return computeBaseSetScore(c,input,null);
    }

    @Override
    public double computeBaseSetScore(Configuration c, PairwiseSimilarity set, double[] window) {
       // DoubleMatrix1D permutation = Vectors.permutation(set.getVector());
       // DoubleMatrix1D values = new DenseDoubleMatrix1D((int)set.getVector().size());

       // double lovaszScore = 0;
       // for (int i = 1; i < set.getVector().size(); i++) {
       //     lovaszScore += set.getVector().get((int) permutation.get(i)) * (getSetFunction().F(values, permutation, 0, i) - getSetFunction().F(values, permutation, 0, i - 1));
       // }
        //return lovaszScore;
        return 0d;
    }



    @Override
    public DoubleMatrix1D getGradient(Configuration c, PairwiseSimilarity input) {
        return null;
    }

    public double computeScore(Configuration c) {
        //PairwiseSimilarity b= new PairwiseSimilarity(getDocument());
        //return computeBaseSetScore(c,b,null);
        return 0d;
    }

    public double computeScore(Configuration c, double[] window) {
        //PairwiseSimilarity b= new PairwiseSimilarity(getDocument());
        //return computeBaseSetScore(c,b,window);
        return 0d;
    }
}
