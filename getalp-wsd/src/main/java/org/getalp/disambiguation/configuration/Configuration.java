package org.getalp.disambiguation.configuration;

import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.disambiguation.Document;

public class Configuration {
    int[] assignments;
    DoubleMatrix1D confidence;

    public Configuration(Document d) {
        assignments = new int[d.getWords().size()];
        confidence = new DenseDoubleMatrix1D(d.getWords().size());
    }
    public Configuration(Configuration d) {
        assignments = d.assignments.clone();
        confidence = d.confidence.copy();
    }

    public void setSense(int wordIndex, int senseIndex){
        assignments[wordIndex] = senseIndex;
    }

    public void setConfidence(int wordIndex, double confidence){
        this.confidence.set(wordIndex,confidence);
    }

    public int getAssignment(int wordIndex){
        return assignments[wordIndex];
    }

    public double getConfidence(int wordIndex){
        return confidence.get(wordIndex);
    }

    public int size(){
        return  assignments.length;
    }

    public Configuration clone(){
        return new Configuration(this);
    }
}
