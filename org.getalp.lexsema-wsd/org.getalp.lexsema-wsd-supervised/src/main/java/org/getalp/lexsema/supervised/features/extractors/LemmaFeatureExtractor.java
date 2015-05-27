package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.similarity.Document;

import java.util.ArrayList;
import java.util.List;


public class LemmaFeatureExtractor implements LocalTextFeatureExtractor {

    private final boolean useLemmas;
    private int lemmamin;
    private int lemmamax;

    public LemmaFeatureExtractor(int lemmamin, int lemmamax) {
        this(lemmamin, lemmamax, true);
    }

    public LemmaFeatureExtractor(int lemmamin, int lemmamax, boolean useLemmas) {
        this.lemmamin = lemmamin;
        this.lemmamax = lemmamax;
        this.useLemmas = useLemmas;
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
                    String lemma = null;
                    if (useLemmas) {
                        lemma = document.getWord(0, j).getLemma();
                    }
                    if (lemma != null) {
                        lemma = document.getWord(0, j).getSurfaceForm();
                    }
                    lemmaFeature = "\"" + lemma;
                }
                features.add(lemmaFeature);
            }
        }
        return features;
    }
}
