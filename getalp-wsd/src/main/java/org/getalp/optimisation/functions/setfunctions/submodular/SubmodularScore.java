package org.getalp.optimisation.functions.setfunctions.submodular;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.optimisation.functions.cache.SetFunctionCache;
import org.getalp.optimisation.functions.setfunctions.SetFunction;

/**
 * Created by tchechem on 10/1/14.
 */
public abstract class SubmodularScore {

    private Document document;
    private SetFunction setFunction;
    private SetFunctionCache oracleCache;

    protected SubmodularScore(Document d, SetFunction setFunction) {
        document = d;
        this.setFunction = setFunction;
        oracleCache = new SetFunctionCache();
    }

    public abstract double computeBaseSetScore(Configuration c, PairwiseSimilarity input);
    public abstract double computeBaseSetScore(Configuration c, PairwiseSimilarity input, double[] window);
    public abstract DoubleMatrix1D getGradient(Configuration c, PairwiseSimilarity input);

    protected Document getDocument() {
        return document;
    }

    protected SetFunction getSetFunction() {
        return setFunction;
    }

    protected SetFunctionCache getOracleCache() {
        return oracleCache;
    }
}
