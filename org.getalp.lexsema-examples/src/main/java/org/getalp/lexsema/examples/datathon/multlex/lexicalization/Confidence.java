package org.getalp.lexsema.examples.datathon.multlex.lexicalization;

public enum Confidence {
    NONE(0d),LOW(.2d), MEDIUM(.5d), HIGH(.8), ABSOLUTE(1d);

    Confidence(double threshold) {
        this.threshold = threshold;
    }

    private double threshold;

    public double getThreshold() {
        return threshold;
    }

    public static Confidence fromTreshold(double threshold){
        if(threshold ==0 ) {
            return NONE;
        }
        else if(threshold >0 && threshold <.2) {
            return LOW;
        } else if (threshold<.5){
            return MEDIUM;
        } else if(threshold<.8){
            return  HIGH;
        } else if (threshold>.8) {
            return ABSOLUTE;
        }
        return NONE;
    }
}
