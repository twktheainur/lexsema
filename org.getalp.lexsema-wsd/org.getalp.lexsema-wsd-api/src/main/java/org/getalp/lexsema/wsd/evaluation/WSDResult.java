package org.getalp.lexsema.wsd.evaluation;

public interface WSDResult {
    double getPrecision();

    double getRecall();

    double getF1Score();
}
