package org.getalp.lexsema.supervised.features;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynset;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class BabelLemmaTranslateSemCorTrainingDataExtractor implements TrainingDataExtractor {

    private static Logger logger = LoggerFactory.getLogger(BabelLemmaTranslateSemCorTrainingDataExtractor.class);
    private LocalTextFeatureExtractor localTextFeatureExtractor;
    private Map<String, List<List<String>>> instanceVectors;

    private Map<String, String> senseTagMap;

    private BabelNet babelNet;
    private Language language;

    private int translated = 0;
    private int notFound = 0;


    public BabelLemmaTranslateSemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor, File senseTagMappingFile, Language language) {
        this.localTextFeatureExtractor = localTextFeatureExtractor;
        instanceVectors = new HashMap<>();
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
        } catch (IOException e) {
            logger.error("Error while loading the sense mapping, aborting.");
        }
        babelNet = BabelNet.getInstance();
        this.language = language;
    }


    @SuppressWarnings("all")
    @Override
    public void extract(Iterable<Text> annotatedCorpus) {

            /* The file does not exist, therefore we most extract the features and write the data file*/
        for (Text text : annotatedCorpus) {
            int wordIndex = 0;
            for (Word w : text) {
                String lemma = w.getLemma();
                List<String> instance = localTextFeatureExtractor.getFeatures(text, wordIndex);
                String semanticTag = w.getSemanticTag();
                if (semanticTag != null) {
                    String semanticTagkey = String.format("%s%%%s", lemma, semanticTag);
                    if (senseTagMap.containsKey(semanticTagkey)) {
                        semanticTag = senseTagMap.get(semanticTagkey);
                    }
                    if (language != null) {
                        try {
                            BabelSynset babelSynset = babelNet.getSynsetFromId(semanticTag);
                            if (babelSynset != null) {
                                List<BabelSense> babelSenses = babelSynset.getSenses(toBabelNetLanguage(language));
                                if (babelSenses != null && !babelSenses.isEmpty()) {
                                    String langlema = babelSenses.get(0).getLemma();
                                    if (langlema != null) {
                                        //logger.info(String.format("French lemma found for %s: %s",lemma, langlema));
                                        lemma = langlema;
                                        w.setLemma(lemma);
                                        translated++;
                                    } else {
                                        //logger.info(String.format("No French lemma found for %s",lemma));
                                        notFound++;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                instance.add(0, String.format("\"%s\"", semanticTag));
                if (!instanceVectors.containsKey(lemma)) {
                    instanceVectors.put(lemma, new ArrayList<List<String>>());
                }
                instanceVectors.get(lemma).add(instance);
                wordIndex++;
            }
        }
        int totalMappings = notFound + translated;
        double pctSuccessful = ((double) translated / (double) totalMappings) * 100d;
        double pctFailed = ((double) notFound / (double) totalMappings) * 100d;
        logger.info(String.format("Translated= %d [%.2f%%] | Failed = %d [%.2f%%] | Total = %d", translated, pctSuccessful, notFound, pctFailed, totalMappings));
    }

    private it.uniroma1.lcl.jlt.util.Language toBabelNetLanguage(Language language) {
        return it.uniroma1.lcl.jlt.util.Language.valueOf(language.getISO2Code().toUpperCase());
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
