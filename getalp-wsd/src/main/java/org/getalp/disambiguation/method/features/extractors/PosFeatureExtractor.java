package org.getalp.disambiguation.method.features.extractors;

import org.getalp.io.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchechem on 11/5/14.
 */
public class PosFeatureExtractor implements LocalTextFeatureExtractor {

    private int posmin;
    private int posmax;

    public PosFeatureExtractor(int posmin, int posmax) {
        this.posmin = posmin;
        this.posmax = posmax;
    }

    @Override
    public List<String> getFeatures(Document document, int currentIndex) {
        List<String> features = new ArrayList<>();
        for (int j = currentIndex - posmin; j <= currentIndex + posmax; j++) {
            if (j != currentIndex) {
                String posFeature;
                if (j < 0 || j >= document.getLexicalEntries().size()) {
                    posFeature = "\"ε\"";
                } else {
                    posFeature = "\"" + convertPos(document.getLexicalEntries().get(j).getPos()) + "\"";
                }
                features.add(posFeature);
            }
        }
        return features;
    }

    public String convertPos(String pos) {
        String converted = "";
        if (pos.equals("n")) {
            converted = "NN";
        } else if (pos.equals("v")) {
            converted = "VB";
        } else if (pos.equals("a")) {
            converted = "JJ";
        } else if (pos.equals("r")) {
            converted = "RB";
        }
        return converted;
    }
}
