package org.getalp.lexsema.wsd.score;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;

public class ACSimilarityConfigurationScorer implements ConfigurationScorer {

    private SimilarityMeasure similarityMeasure;

    public ACSimilarityConfigurationScorer(SimilarityMeasure similarityMeasure) {
        this.similarityMeasure = similarityMeasure;
    }

    @Override
    public double computeScore(Document d, Configuration c) {

        double totalScore = 0d;
        for (int i = 0; i < c.size(); i++) {
            if (c.getAssignment(i) != -1) {
                double similarity = computeSimilarity(i, c, d);
                //System.err.println("Word "+(i+1)+" score = "+similarity);
                totalScore += similarity;
            }
        }
        return totalScore;
    }

    private double computeSimilarity(int wordIndex, Configuration configuration, Document document) {
        double totalScore = 0.0D;
        if(!document.getSenses(configuration.getStart(),wordIndex).isEmpty()) {
            Sense senseA = document.getSenses(configuration.getStart(), wordIndex)
                    .get(configuration.getAssignment(wordIndex));
            Sense senseB;
            int index = 0;
            for (int i = wordIndex; i < configuration.size(); ++i) {
                if (wordIndex != i) {
                    if(!document.getSenses(configuration.getStart(), i).isEmpty()) {
                        senseB = document.getSenses(configuration.getStart(), i)
                                .get(configuration.getAssignment(i));
                        double score = senseA.computeSimilarityWith(similarityMeasure, senseB);
                        //System.err.println(String.format("\tScore(%d,%d)=%f",wordIndex,i,score));
                        if(!Double.isNaN(score)) {
                            totalScore += score;
                        }
                    }
                }
            }
        }
        return totalScore;
    }

    @Override
    public void release() {

    }
}
