package org.getalp.disambiguation.configuration;

import org.getalp.io.Document;

public class SubConfiguration extends Configuration {

    int start;
    int end;

    public SubConfiguration(Document d, int start, int end) {
        super(d);
        this.start = start;
        if (end > super.size()) {
            this.end = super.size();
        } else {
            this.end = end;
        }

    }

    public SubConfiguration(SubConfiguration d) {
        super(d);
        start = d.getStart();
        if (end > super.size()) {
            end = super.size();
        } else {
            end = d.getEnd();
        }
    }

    @Override
    public void setSense(int wordIndex, int senseIndex) {
        if (wordIndex + start < end) {
            assignments[wordIndex + start] = senseIndex;
        }
    }

    @Override
    public void setConfidence(int wordIndex, double confidence) {
        if (wordIndex + start < end) {
            this.confidence.set(start + wordIndex, confidence);
        }
    }

    @Override
    public int getAssignment(int wordIndex) {
        if (wordIndex + start < end) {
            return assignments[wordIndex + start];
        } else {
            return -1;
        }
    }

    @Override
    public double getConfidence(int wordIndex) {
        if (wordIndex + start < end) {
            return confidence.get(wordIndex + start);
        } else {
            return -1;
        }
    }


    @Override
    public int size() {
        return end - start;
    }

    @Override
    public SubConfiguration clone() throws CloneNotSupportedException {
        super.clone();
        return new SubConfiguration(this);
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    public Configuration getConfiguration() {
        return new Configuration(this);
    }
}
