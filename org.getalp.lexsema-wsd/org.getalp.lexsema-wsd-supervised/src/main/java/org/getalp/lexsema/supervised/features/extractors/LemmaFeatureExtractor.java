package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.similarity.Document;

import java.util.ArrayList;
import java.util.List;


public class LemmaFeatureExtractor implements LocalTextFeatureExtractor {

    private int lemmamin;
    private int lemmamax;

    public LemmaFeatureExtractor(int lemmamin, int lemmamax) {
        this.lemmamin = lemmamin;
        this.lemmamax = lemmamax;
    }

    @Override
    public List<String> getFeatures(Document document, int currentIndex) {
        List<String> features = new ArrayList<>();
        for (int j = currentIndex - lemmamin; j <= currentIndex + lemmamax; j++) {
            if (j != currentIndex) {
                String lemmaFeature;
                if (j < 0 || j >= document.size()) {
                    lemmaFeature = "\"X\"";
                } else {
                    lemmaFeature = "\"" + document.getWord(0, j).getLemma();
                }
                features.add(lemmaFeature);
            }
        }
        return features;
    }
}
