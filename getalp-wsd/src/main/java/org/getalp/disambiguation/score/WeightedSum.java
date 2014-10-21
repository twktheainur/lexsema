package org.getalp.disambiguation.score;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.similarity.local.SimilarityMeasure;

import java.util.List;

public class WeightedSum implements GlobalScore {
    Document document;
    SimilarityMeasure sim;

    public WeightedSum(Document document, SimilarityMeasure sim) {
        this.document = document;
        this.sim = sim;
    }

    @Override
    public double computeScore(Configuration c) {
        double[] window = new double[c.size()];
        for (int i = 0; i < window.length; i++) {
            window[i] = 1;
        }
        return computeScore(c, window);
    }

    @Override
    public double computeScore(Configuration c, double[] window) {
        double score = 0d;
        for (int i = 0; i < c.size(); i++) {
            for (int j = 0; i < c.size(); j++) {
                if (window[j] > 0.0001d) {
                    List<String> defA = document.getSenses().get(i).get(c.getAssignment(i)).getSignature();
                    List<String> defB = document.getSenses().get(j).get(c.getAssignment(j)).getSignature();
                    score += window[j] * sim.compute(defA, defB);
                }
            }
        }
        return score;
    }
}
