package org.getalp.lexsema.wsd.configuration;


import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import org.getalp.lexsema.similarity.Document;

import java.util.Random;

public class ConfidenceConfiguration implements Configuration {
    int[] assignments;
    DoubleMatrix1D confidence;
    Document document;

    public ConfidenceConfiguration(Document d) {
        int documentSize = d.size();
        assignments = new int[documentSize];
        noAssignmentInit(documentSize);
        confidence = new DenseDoubleMatrix1D(documentSize);
        document = d;
    }

    public ConfidenceConfiguration(Document d, InitializationType initializationType) {
        int documentSize = d.size();
        assignments = new int[documentSize];
        switch (initializationType) {
            case FIRST:
                firstAssignment(documentSize);
                break;
            case NONE:
                noAssignmentInit(documentSize);
                break;
            case RANDOM:
                randomAssignment(d);
                break;
        }
        confidence = new DenseDoubleMatrix1D(documentSize);
        document = d;
    }

    @SuppressWarnings("MethodParameterOfConcreteClass") // Copy constructor
    public ConfidenceConfiguration(final ConfidenceConfiguration d) {
        assignments = d.assignments.clone();
        confidence = d.confidence.copy();
        document = d.getDocument();
    }

    private void noAssignmentInit(int documentSize) {
        for (int i = 0; i < documentSize; i++) {
            assignments[i] = -1;
        }
    }

    private void firstAssignment(int documentSize) {
        for (int i = 0; i < documentSize; i++) {
            assignments[i] = 0;
        }
    }

    private void randomAssignment(Document d) {
        int documentSize = d.size();
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < documentSize; i++) {
            int numSenses = d.getSenses(i).size();
            if (numSenses > 1) {
                assignments[i] = r.nextInt(numSenses);
            } else {
                assignments[i] = 0;
            }
        }
    }

    @Override
    public void setSense(int wordIndex, int senseIndex) {
        assignments[wordIndex] = senseIndex;
    }

    @Override
    public void setConfidence(int wordIndex, double confidence) {
        this.confidence.set(wordIndex, confidence);
    }

    @Override
    public int getAssignment(int wordIndex) {
        return assignments[wordIndex];
    }

    @Override
    public double getConfidence(int wordIndex) {
        return confidence.get(wordIndex);
    }

    @Override
    public int size() {
        return assignments.length;
    }

    @Override
    public Configuration clone() throws CloneNotSupportedException {
        super.clone();
        return new ConfidenceConfiguration(this);
    }

    @Override
    public int getStart() {
        return 0;
    }

    @Override
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

    @Override
    public void initialize(int value) {
        for (int i = getStart(); i < getEnd(); i++) {
            assignments[i] = value;
        }
    }

    @Override
    public int countUnassigned() {
        int unassignedCount = 0;
        for (int assignment : assignments) {
            if (assignment == -1) {
                unassignedCount++;
            }
        }
        return unassignedCount;
    }

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public int[] getAssignments() {
        return assignments;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    public enum InitializationType {
        NONE, FIRST, RANDOM
    }
}
