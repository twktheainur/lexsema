package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.supervised.features.ContextWindow;

import java.util.ArrayList;
import java.util.List;

public class LocalCollocationFeatureExtractor implements LocalTextFeatureExtractor {

    private List<ContextWindow> contextWindows;
    private boolean useLemmas = true;

    public LocalCollocationFeatureExtractor(List<ContextWindow> contextWindows) {
        this(contextWindows, true);
    }

    public LocalCollocationFeatureExtractor(List<ContextWindow> contextWindows, boolean considerLemmas) {
        this.contextWindows = contextWindows;
        useLemmas = considerLemmas;
    }

    @Override
    public List<String> getFeatures(Document document, int currentIndex) {
        List<String> features = new ArrayList<>();
        for (int a = 0; a < contextWindows.size(); a++) {
            for (int j = currentIndex - contextWindows.get(a).getMin(); j <= currentIndex + contextWindows.get(a).getMax(); j++) {
                String colFeature;
                if (currentIndex != j) {
                    if (j < 0 || j >= document.size()) {
                        colFeature = "\"∅\"";
                    } else {
                        String lemma = null;
                        if (useLemmas) {
                            lemma = document.getWord(0, j).getLemma();
                        }
                        if (lemma == null) {
                            lemma = document.getWord(0, j).getSurfaceForm();
                        }
                        colFeature = "\"" + lemma + "\"";
                    }
                    features.add(colFeature);
                }
            }
        }
        return features;
    }
}
