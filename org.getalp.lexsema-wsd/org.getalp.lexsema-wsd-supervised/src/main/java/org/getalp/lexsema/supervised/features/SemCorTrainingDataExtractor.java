package org.getalp.lexsema.supervised.features;

import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;

import java.io.*;
import java.util.*;

public class SemCorTrainingDataExtractor implements TrainingDataExtractor {
    private LocalTextFeatureExtractor localTextFeatureExtractor;
    private Map<String, List<List<String>>> instanceVectors;

    public SemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor) {
        this.localTextFeatureExtractor = localTextFeatureExtractor;
        instanceVectors = new HashMap<>();
    }

    @Override
    public void extract(Iterable<Text> annotatedCorpus) {

        File instanceFile = new File("instances.data");
        File attributeFile = new File("attributes.data");

        if (instanceFile.exists() && attributeFile.exists()) {
            FileInputStream instanceFis = null;
            FileInputStream attributeFis = null;
            try {
                instanceFis = new FileInputStream(instanceFile);
                ObjectInputStream instanceOis = new ObjectInputStream(instanceFis);
                instanceVectors = (Map<String, List<List<String>>>) instanceOis.readObject();
                instanceOis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            for (Text text : annotatedCorpus) {
                int wordIndex = 0;
                for (Word w : text) {
                    String lemma = w.getLemma();
                    List<String> instance = localTextFeatureExtractor.getFeatures(text, wordIndex);
                    instance.add(0, String.format("\"%s\"", w.getSemanticTag()));
                    if (!instanceVectors.containsKey(lemma)) {
                        instanceVectors.put(lemma, new ArrayList<List<String>>());
                    }
                    instanceVectors.get(lemma).add(instance);
                    wordIndex++;
                }
            }
        }
        //Serializing instance vectors
        FileOutputStream instancesfos;
        try {
            instancesfos = new FileOutputStream(instanceFile);

            ObjectOutputStream instancesoos = new ObjectOutputStream(instancesfos);
            instancesoos.writeObject(instanceVectors);
            instancesoos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
