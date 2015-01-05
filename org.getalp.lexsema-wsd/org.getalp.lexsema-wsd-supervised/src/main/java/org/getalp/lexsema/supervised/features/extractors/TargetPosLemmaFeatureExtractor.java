package org.getalp.lexsema.supervised.features.extractors;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Word;

import java.util.ArrayList;
import java.util.List;

public class TargetPosLemmaFeatureExtractor implements LocalTextFeatureExtractor {
    @Override
    public List<String> getFeatures(Document document, int currentIndex) {
        List<String> features = new ArrayList<>();
        Word currentEntry = document.getWord(0, currentIndex);
        String lemma = currentEntry.getLemma();
        if (lemma != null) {
            lemma = currentEntry.getSurfaceForm();
        }
        features.add(lemma);
        features.add(currentEntry.getPartOfSpeech());
        return features;
    }
}
