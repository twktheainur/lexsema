package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.io.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchechem on 11/5/14.
 */
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
                if (j < 0 || j >= document.getLexicalEntries().size()) {
                    lemmaFeature = "\"X\"";
                } else {
                    lemmaFeature = "\"" + document.getLexicalEntries().get(j).getLemma();
                }
                features.add(lemmaFeature);
            }
        }
        return features;
    }
}
