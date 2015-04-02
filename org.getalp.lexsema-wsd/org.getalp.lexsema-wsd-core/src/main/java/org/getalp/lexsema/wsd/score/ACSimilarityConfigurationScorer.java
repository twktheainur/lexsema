package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.wsd.configuration.Configuration;

public class ACSimilarityConfigurationScorer implements ConfigurationScorer {

    private SimilarityMeasure similarityMeasure;

    private ACSimilarityConfigurationScorer(SimilarityMeasure similarityMeasure) {
        this.similarityMeasure = similarityMeasure;
    }

    @Override
    public double computeScore(Document d, Configuration c) {

        double totalScore = 0d;
        for (int i = 0; i < c.size(); i++) {
            if (c.getAssignment(i) != -1) {
                totalScore += computeSimilarity(i, c, d);
            }
        }
        return totalScore;
    }

    private double computeSimilarity(int wordIndex, Configuration configuration, Document document) {
        int[] possibilities = configuration.getAssignments();
        SemanticSignature sigA = document.getSenses(wordIndex)
                .get(possibilities[wordIndex]).getSemanticSignature();
        SemanticSignature nextSig;
        int index = 0;
        double totalScore = 0.0D;

        for (int i = index; i < configuration.size(); ++i) {
            nextSig = document.getSenses(i)
                    .get(possibilities[i]).getSemanticSignature();
            totalScore += similarityMeasure.compute(sigA, nextSig, null, null);
        }
        return totalScore;
    }

    @Override
    public void release() {

    }
}
