package org.getalp.lexsema.supervised.features;

import org.getalp.lexsema.similarity.Text;

import java.util.List;

public interface TrainingDataExtractor {
    void extract(Iterable<Text> annotatedCorpus);

    List<List<String>> getWordFeaturesInstances(String lemma);
    List<List<String>> getSensesFeaturesInstances(List<String> senseTags);

    public List<String> getAttributes(String lemma);
}
