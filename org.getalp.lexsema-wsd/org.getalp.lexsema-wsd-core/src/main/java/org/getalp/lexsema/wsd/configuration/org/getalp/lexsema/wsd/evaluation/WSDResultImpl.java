package org.getalp.lexsema.wsd.configuration.org.getalp.lexsema.wsd.evaluation;

import org.getalp.lexsema.similarity.Document;

public class WSDResultImpl implements WSDResult {
    private final double PERCENT = 100d;
    private double precision;
    private double recall;
    private double f1Score;
    private int provided;
    private String textId;
    private int expected;
    private int correct;

    public WSDResultImpl(int provided, int expected, int correct, Document document) {
        this.correct = correct;
        this.expected = expected;
        this.provided = provided;
        textId = document.getId();
        precision = (double) correct / (double) provided;
        recall = (double) correct / (double) expected;
        f1Score = 2 * recall * precision / (recall + precision);
    }

    @Override
    public double getPrecision() {
        return precision;
    }

    @Override
    public double getRecall() {
        return recall;
    }

    @Override
    public double getF1Score() {
        return f1Score;
    }

    @Override
    public String toString() {
        return String.format("[%s | A = %d of %d (%.2f)] P=%.4f%% (%d of %d) \tR=%.4f%% (%d of %d)\tF1=%.4f%%", textId,
                provided, expected, (double) provided / (double) expected,
                precision * PERCENT, correct, provided,
                recall * PERCENT, correct, expected, f1Score * PERCENT);
    }
}
