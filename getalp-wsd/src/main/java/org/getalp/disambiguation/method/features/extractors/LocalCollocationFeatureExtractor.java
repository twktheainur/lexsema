package org.getalp.disambiguation.method.features.extractors;

import org.getalp.disambiguation.method.features.ContextWindow;
import org.getalp.io.Document;

import java.util.ArrayList;
import java.util.List;

public class LocalCollocationFeatureExtractor implements LocalTextFeatureExtractor {

    private List<ContextWindow> contextWindows;

    public LocalCollocationFeatureExtractor(List<ContextWindow> contextWindows) {
        this.contextWindows = contextWindows;
    }

    @Override
    public List<String> getFeatures(Document document, int currentIndex) {
        List<String> features = new ArrayList<>();
        for (int a = 0; a < contextWindows.size(); a++) {
            for (int j = currentIndex - contextWindows.get(a).getMin(); j <= currentIndex + contextWindows.get(a).getMax(); j++) {
                String colFeature;
                if (currentIndex != j) {
                    if (j < 0 || j >= document.getLexicalEntries().size()) {
                        colFeature = "\"∅\"";
                    } else {
                        colFeature = "\"" + document.getLexicalEntries().get(j).getLemma() + "\"";
                    }
                    features.add(colFeature);
                }
            }
        }
        return features;
    }
}
