package org.getalp.lexsema.supervised.features;

import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class BabelNetSemCorTrainingDataExtractor implements TrainingDataExtractor {

    private static final Logger logger = LoggerFactory.getLogger(BabelNetSemCorTrainingDataExtractor.class);
    private final LocalTextFeatureExtractor localTextFeatureExtractor;
    private final Map<String, List<List<String>>> instanceVectors;
    private Map<String, List<String>> instanceVectorsSenses;

    private Map<String, String> senseTagMap;

    private int successfulMappings;
    private int unsuccessfulMappings;

    public BabelNetSemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor) {
        this.localTextFeatureExtractor = localTextFeatureExtractor;
        instanceVectors = new HashMap<>();
    }

    public BabelNetSemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor, File senseTagMappingFile) {
        this.localTextFeatureExtractor = localTextFeatureExtractor;
        instanceVectors = new HashMap<>();
        instanceVectorsSenses = new HashMap<>();
        senseTagMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(senseTagMappingFile))) {
            String line = "";
            do {
                line = reader.readLine();
                if (line != null) {
                    String[] entry = line.split("\t");
                    try {
                        senseTagMap.put(entry[0], entry[1]);
                    } catch (ArrayIndexOutOfBoundsException ignore) {
                    }
                }
            } while (line != null && !line.isEmpty());
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        } catch (IOException ignored) {
            logger.error("Error while loading the sense mapping, aborting.");
        }
    }


    @SuppressWarnings("all")
    @Override
    public void extract(Iterable<Text> annotatedCorpus) {
            /* The file does not exist, therefore we most extract the features and write the data file*/
        for (Text text : annotatedCorpus) {
            int wordIndex = 0;
            for (Word w : text) {
                String lemma = w.getSurfaceForm();
                List<String> instance = localTextFeatureExtractor.getFeatures(text, wordIndex);
                String semanticTag = w.getSenseAnnotation();
                if (semanticTag != null && senseTagMap != null) {
                    String semanticTagkey = String.format("%s%%%s", lemma, semanticTag);
                    if (senseTagMap.containsKey(semanticTagkey)) {
                        semanticTag = senseTagMap.get(semanticTagkey);
                        successfulMappings++;
                    } else {
                        unsuccessfulMappings++;
                    }
                }
                instance.add(0, String.format("\"%s\"", semanticTag));
                if (!instanceVectors.containsKey(lemma)) {
                    instanceVectors.put(lemma, new ArrayList<List<String>>());
                }
                if(!instanceVectorsSenses.containsKey(semanticTag)){
                    instanceVectorsSenses.put(semanticTag, instance);
                }
                instanceVectors.get(lemma).add(instance);
                wordIndex++;
            }
        }
        int totalMappings = unsuccessfulMappings + successfulMappings;
        double pctSuccessful = ((double) successfulMappings / (double) totalMappings) * 100d;
        double pctFailed = ((double) unsuccessfulMappings / (double) totalMappings) * 100d;
        logger.info(String.format("Successful= %d [%.2f%%] | Failed = %d [%.2f%%] | Total = %d", successfulMappings, pctSuccessful, unsuccessfulMappings, pctFailed, totalMappings));
    }

    @Override
    public List<List<String>> getWordFeaturesInstances(String lemma) {
        return instanceVectors.get(lemma);
    }

    @Override
    public List<List<String>> getSensesFeaturesInstances(List<String> senseTags) {
        List<List<String>> result = new ArrayList<>();
        for(String tag: senseTags) {
            if(instanceVectorsSenses.containsKey(tag)) {
                result.add(instanceVectorsSenses.get(tag));
            } else {
                result.add(Collections.emptyList());
            }
        }
        return result;
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
