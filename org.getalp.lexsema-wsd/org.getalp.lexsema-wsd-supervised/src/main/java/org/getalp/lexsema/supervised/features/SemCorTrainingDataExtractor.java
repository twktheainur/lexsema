package org.getalp.lexsema.supervised.features;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SemCorTrainingDataExtractor implements TrainingDataExtractor {

    private static final Logger logger = LoggerFactory.getLogger(SemCorTrainingDataExtractor.class);
    private final LocalTextFeatureExtractor localTextFeatureExtractor;
    private final Map<String, List<List<String>>> instanceVectors;
    private final boolean useSurfaceForm;

    public SemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor) {
        this(localTextFeatureExtractor, false);
    }

    public SemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor, boolean useSurfaceForm) {
        this.useSurfaceForm = useSurfaceForm;
        this.localTextFeatureExtractor = localTextFeatureExtractor;
        instanceVectors = new HashMap<>();
    }

    @SuppressWarnings("all")
    @Override
    public void extract(Iterable<Text> annotatedCorpus) {

        logger.info("Extracting features...");

        for (Text text : annotatedCorpus) {
            final AtomicInteger wordIndex = new AtomicInteger(0);

            text.words().parallelStream().forEach(w -> {
                processWord(w, text, wordIndex.getAndIncrement());
            });/*
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }


    private void processWord(Word w, Document text, int wordIndex) {
        String semanticTag = w.getSenseAnnotation();
        if(semanticTag!=null && !semanticTag.isEmpty()) {
            String lemma = w.getLemma();
            String surfaceForm = w.getSurfaceForm();
            List<String> instance = localTextFeatureExtractor.getFeatures(text, wordIndex);

            instance.add(0, "\"" + semanticTag + "\"");
            String key;
            if (useSurfaceForm) {
                key = surfaceForm;
            } else {
                key = lemma;
            }
            synchronized (instanceVectors) {
                if (!instanceVectors.containsKey(key)) {
                    instanceVectors.put(key, new ArrayList<>());
                }
                instanceVectors.get(key).add(instance);
            }
       }
    }

    @Override
    public List<List<String>> getWordFeaturesInstances(String lemma) {
        return instanceVectors.get(lemma);
    }

    @Override
    public List<String> getAttributes(String lemma) {
        List<String> attributes = new ArrayList<>();
        List<List<String>> instances = getWordFeaturesInstances(lemma);
        int size = 0;
        if (!instances.isEmpty()) {
            size = instances.get(0).size();
        }
        attributes.add("SENSE");
        for (int i = 1; i < size; i++) {
            attributes.add(String.format("C%d", i));
        }
        return Collections.unmodifiableList(attributes);
    }
}
