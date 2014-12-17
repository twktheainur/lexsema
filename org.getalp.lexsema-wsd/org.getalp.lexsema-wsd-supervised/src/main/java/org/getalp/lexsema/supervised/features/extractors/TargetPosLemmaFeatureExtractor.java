package org.getalp.lexsema.supervised.features.extractors;


import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.similarity.Document;

import java.util.ArrayList;
import java.util.List;

public class TargetPosLemmaFeatureExtractor implements LocalTextFeatureExtractor {
    @Override
    public List<String> getFeatures(Document document, int currentIndex) {
        List<String> features = new ArrayList<>();
        LexicalEntry currentEntry = document.getWord(0, currentIndex);
        features.add(currentEntry.getLemma());
        features.add(currentEntry.getPartOfSpeech());
        return features;
    }
}
