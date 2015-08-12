package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.similarity.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tchechem on 11/5/14.
 */
public class AggregateLocalTextFeatureExtractor implements LocalTextFeatureExtractor {

    List<LocalTextFeatureExtractor> extractors;


    public AggregateLocalTextFeatureExtractor() {
        extractors = new ArrayList<>();
    }

    public void addExtractor(LocalTextFeatureExtractor e) {
        extractors.add(e);
    }

    @Override
    public List<String> getFeatures(Document d, int currentIndex) {
        List<String> features = new ArrayList<>();
        for (LocalTextFeatureExtractor fe : extractors) {
            features.addAll(fe.getFeatures(d, currentIndex));
        }
        return features;
    }

   /* public String toString(){

        String s = "";
        for(LocalTextFeatureExtractor ltfe:extractors)
            for(String features:ltfe){
                s+=features;
            }
        return s;
    }*/
}
