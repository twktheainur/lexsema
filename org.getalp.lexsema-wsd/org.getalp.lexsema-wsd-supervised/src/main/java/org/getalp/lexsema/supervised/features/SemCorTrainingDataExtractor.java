package org.getalp.lexsema.supervised.features;

import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class SemCorTrainingDataExtractor implements TrainingDataExtractor {

    private static Logger logger = LoggerFactory.getLogger(SemCorTrainingDataExtractor.class);
    private LocalTextFeatureExtractor localTextFeatureExtractor;
    private Map<String, List<List<String>>> instanceVectors;
    private boolean useSurfaceForm;

    public SemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor) {
        this(localTextFeatureExtractor, false);
    }

    public SemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor, boolean useSurfaceForm) {
        this.localTextFeatureExtractor = localTextFeatureExtractor;
        instanceVectors = new HashMap<>();
        this.useSurfaceForm = useSurfaceForm;
    }


    @SuppressWarnings("all")
    @Override
    public void extract(Iterable<Text> annotatedCorpus) {

        File instanceFile = new File("instances.data");
        File attributeFile = new File("attributes.data");

        if (instanceFile.exists() && attributeFile.exists()) {
            FileInputStream instanceFis;
            ObjectInput instanceOis = null;
            try {
                //noinspection IOResourceOpenedButNotSafelyClosed
                instanceFis = new FileInputStream(instanceFile);
                //noinspection IOResourceOpenedButNotSafelyClosed
                instanceOis = new ObjectInputStream(instanceFis);
                instanceVectors = (Map<String, List<List<String>>>) instanceOis.readObject();

            } catch (FileNotFoundException e) {
                logger.error("instances.dat deleted during execution!");
            } catch (ClassNotFoundException | IOException e) {
                logger.error(e.getLocalizedMessage());
            } finally {
                assert instanceOis != null;
                try {
                    instanceOis.close();
                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage());
                }
            }

        } else {
            /* The file does not exist, therefore we most extract the features and write the data file*/
            for (Text text : annotatedCorpus) {
                int wordIndex = 0;
                for (Word w : text) {
                    String lemma = w.getLemma();
                    String surfaceForm = w.getSurfaceForm();
                    List<String> instance = localTextFeatureExtractor.getFeatures(text, wordIndex);
                    String semanticTag = w.getSenseAnnotation();
                    instance.add(0, String.format("\"%s\"", semanticTag));
                    String key = "";
                    if (useSurfaceForm) {
                        key = surfaceForm;
                    } else {
                        key = lemma;
                    }

                    if (!instanceVectors.containsKey(key)) {
                        instanceVectors.put(key, new ArrayList<List<String>>());
                    }
                    instanceVectors.get(key).add(instance);
                    wordIndex++;
                }
            }
        }
        //Serializing instance vectors
        FileOutputStream instancesfos;
        ObjectOutputStream instancesoos = null;
        try {
            instancesfos = new FileOutputStream(instanceFile);
            instancesoos = new ObjectOutputStream(instancesfos);
            instancesoos.writeObject(instanceVectors);

        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        } finally {
            try {
                instancesoos.close();
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
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
