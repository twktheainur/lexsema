package org.getalp.lexsema.wsd.configuration;


import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.lexsema.similarity.Document;

public class Configuration {
    int[] assignments;
    DoubleMatrix1D confidence;

    public Configuration(Document d) {
        assignments = new int[d.size()];
        for (int i = 0; i < d.size(); i++) {
            assignments[i] = -1;
        }
        confidence = new DenseDoubleMatrix1D(d.size());
    }

    public Configuration(Configuration d) {
        assignments = d.assignments.clone();
        confidence = d.confidence.copy();
    }

    public void setSense(int wordIndex, int senseIndex) {
        assignments[wordIndex] = senseIndex;
    }

    public void setConfidence(int wordIndex, double confidence) {
        this.confidence.set(wordIndex, confidence);
    }

    public int getAssignment(int wordIndex) {
        return assignments[wordIndex];
    }

    public double getConfidence(int wordIndex) {
        return confidence.get(wordIndex);
    }

    public int size() {
        return assignments.length;
    }

    @Override
    public Configuration clone() throws CloneNotSupportedException {
        super.clone();
        return new Configuration(this);
    }

    public int getStart() {
        return 0;
    }

    public int getEnd() {
        return assignments.length;
    }

    @Override
    public String toString() {
        String out = "[ ";
        for (int i = getStart(); i < getEnd(); i++) {
            out += assignments[i] + ", ";
        }
        out += "]";
        return out;
    }

    public void initialize(int value) {
        for (int i = getStart(); i < getEnd(); i++) {
            assignments[i] = value;
        }
    }

    public int countUnassigned() {
        int unassignedCount = 0;
        for (int i = 0; i < assignments.length; i++) {
            unassignedCount++;
        }
        return unassignedCount;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public int[] getAssignments() {
        return assignments;
    }
}
