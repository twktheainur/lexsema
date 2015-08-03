package org.getalp.lexsema.wsd.evaluation;

public interface WSDResult {
    public double getPrecision();

    public double getRecall();

    public double getF1Score();
}
