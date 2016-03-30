package org.getalp.lexsema.supervised.features;

import org.getalp.lexsema.similarity.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileTrainingDataExtractor implements TrainingDataExtractor {
    private static Logger logger = LoggerFactory.getLogger(FileTrainingDataExtractor.class);
    private String dataPath;
    private Map<String, List<List<String>>> instanceVectors;
    private List<String> attributes;

    public FileTrainingDataExtractor(String dataPath) {
        this.dataPath = dataPath;
        instanceVectors = new HashMap<>();
    }

    @Override
    public void extract(Iterable<Text> annotatedCorpus) {

    }

    @Override
    public List<String> getAttributes(String lemma) {
        File dataFile = new File(String.format("%s%c%s.csv", dataPath, File.separatorChar, lemma.toLowerCase()));
        String instance = "";
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            instance = br.readLine();
            attributes = new ArrayList<>();
            String[] header = instance.split("\t");
            Collections.addAll(attributes, header);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return Collections.unmodifiableList(attributes);
    }

    @Override
    public List<List<String>> getWordFeaturesInstances(String lemma) {
        File dataFile = new File(String.format("%s%c%s.csv", dataPath, File.separatorChar, lemma.toLowerCase()));
        String instance = "";
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            br.readLine();
            do {
                instance = br.readLine();
            } while (instance != null);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return instanceVectors.get(lemma);
    }

    @Override
    public List<List<String>> getSensesFeaturesInstances(List<String> senseTags) {
        return Collections.emptyList();
    }
}
