package org.getalp.lexsema.supervised;

public class ClassificationOutput implements Comparable<ClassificationOutput> {
    private String key;
    private double frequency;
    private double confidence;

    public ClassificationOutput(String key, double frequency, double confidence) {
        this.key = key;
        this.frequency = frequency;
        this.confidence = confidence;
    }
    
    public String toString(){
    	
    	return "[ " + key + ", " + frequency + ", " + confidence + " ]";
    }

    public ClassificationOutput(String key, double frequency) {
        this.key = key;
        this.frequency = frequency;
        confidence = 1;
    }

    public String getKey() {
        return key;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassificationOutput)) return false;

        ClassificationOutput that = (ClassificationOutput) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }

    @Override
    public int compareTo(ClassificationOutput o) {
        int result = 0;
        int cmp1 = Double.valueOf(o.getFrequency()).compareTo(frequency);
        if (cmp1 != 0) {
            result = cmp1;
        } else {
            result = Double.valueOf(o.getConfidence()).compareTo(confidence);
        }
        return result;
    }
}