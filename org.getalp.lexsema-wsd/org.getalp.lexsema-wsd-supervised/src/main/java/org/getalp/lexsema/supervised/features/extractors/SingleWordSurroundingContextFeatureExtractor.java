package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.similarity.Document;

import java.util.ArrayList;
import java.util.List;


public class SingleWordSurroundingContextFeatureExtractor implements LocalTextFeatureExtractor {

    private final int lemmamin;
    private final int lemmamax;

    public SingleWordSurroundingContextFeatureExtractor(int lemmaMin, int lemmaMax) {
        lemmamin = lemmaMin;
        lemmamax = lemmaMax;
    }

    @Override
    public List<String> getFeatures(Document d, int currentIndex) {
        List<String> features = new ArrayList<>();
        for (int j = currentIndex - lemmamin; j <= currentIndex + lemmamax; j++) {
            if (j == currentIndex) {
                features.add("\"1\"");
            } else {
                features.add("\"0\"");
            }
        }
        return features;
    }
}
