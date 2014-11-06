package org.getalp.disambiguation.method.features.extractors;

import org.getalp.io.Document;

import java.util.List;


public interface LocalTextFeatureExtractor {
    public List<String> getFeatures(Document d, int currentIndex);
}
