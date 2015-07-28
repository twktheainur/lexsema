package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Word;

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
                if (j < 0 || j >= document.size()) {
                    posFeature = "\"ε\"";
                } else {
                    Word word = document.getWord(0, j);
                    String pos =
                    posFeature = "\"" + convertPos(word.getPartOfSpeech()) + "\"";
                }
                features.add(posFeature);
            }
        }
        return features;
    }

    public String convertPos(String pos) {
        String converted = "";
        if (pos != null && !pos.isEmpty()) {
            if (pos.equals("n")) {
                converted = "NN";
            } else if (pos.equals("v")) {
                converted = "VB";
            } else if (pos.equals("a")) {
                converted = "JJ";
            } else if (pos.equals("r")) {
                converted = "RB";
            } else {
                if (pos.startsWith("N")) {
                    converted = "NN";
                } else if (pos.startsWith("V")) {
                    converted = "VB";
                } else if (pos.startsWith("J")) {
                    converted = "JJ";
                } else if (pos.startsWith("R")) {
                    converted = "RB";
                } else {
                    converted = pos;
                }
            }
        } else {
            converted = "ε";
        }
        return converted;
    }
}
