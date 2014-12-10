package org.getalp.lexsema.supervised.entrydisambiguator;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.supervised.weka.FeatureIndex;
import org.getalp.lexsema.supervised.weka.WekaClassifier;
import org.getalp.lexsema.supervised.weka.WekaClassifierSetUp;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WekaLexicalEntryDisambiguator extends SequentialLexicalEntryDisambiguator {
    private String dataPath;
    private Map<String, WekaClassifier> classifiers;
    private FeatureIndex featureIndex;
    private WekaClassifierSetUp classifierSetUp;
    private LocalTextFeatureExtractor featureExtractor;

    public WekaLexicalEntryDisambiguator(Configuration c, Document d, int start, int end, int currentIndex, String dataPath, WekaClassifierSetUp classifierSetUp, Map<String, WekaClassifier> classifiers, LocalTextFeatureExtractor featureExtractor) {
        super(c, d, start, end, currentIndex);
        this.dataPath = dataPath;
        this.classifiers = classifiers;
        featureIndex = new FeatureIndex();
        this.classifierSetUp = classifierSetUp;
        this.featureExtractor = featureExtractor;
    }

    @Override
    public void run() {
        try {
            String targetLemma = getDocument().getWord(0, getCurrentIndex()).getLemma();
            String targetPos = convertPos(getDocument().getWord(0, getCurrentIndex()).getPartOfSpeech());

            List<String> features = featureExtractor.getFeatures(getDocument(), getCurrentIndex());

            List<WekaClassifier.ClassificationEntry> results = runClassifier(targetLemma, features);
            if (results.isEmpty()) {
                getConfiguration().setSense(getCurrentIndex(), -1);
            } else {
                getConfiguration().setSense(getCurrentIndex(), -1);
                int s = -1;
                for (int re = 0; re < results.size() && (s = getMatchingSense(getDocument(), results.get(re).getKey(), getCurrentIndex())) == -1; re++)
                    ;
                getConfiguration().setSense(getCurrentIndex(), s);
            }
            getConfiguration().setConfidence(getCurrentIndex(), 1d);
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public int getMatchingSense(Document d, String tag, int wordIndex) {
        for (int s = 0; s < d.getSenses(wordIndex).size(); s++) {
            Sense cs = d.getSenses(wordIndex).get(s);
            if (cs.getId().contains(tag)) {
                return s;
            }
        }
        return -1;
    }

    private List<WekaClassifier.ClassificationEntry> runClassifier(String lemma, List<String> instance) {
        WekaClassifier classifier;
        if (!classifiers.containsKey(lemma)) {
            classifier = new WekaClassifier(classifierSetUp, dataPath + File.separatorChar + "models" + File.separatorChar + lemma + ".model", false);
            if (!classifier.isClassifierTrained()) {
                try {
                    classifier.loadTrainingData(featureIndex, dataPath + File.separatorChar + lemma + ".csv");
                    classifier.trainClassifier();
                } catch (IOException e) {
                    return new ArrayList<WekaClassifier.ClassificationEntry>();
                }

            }
        } else {
            classifier = classifiers.get(lemma);
        }
        return classifier.classify(featureIndex, instance);
    }

    public String convertPos(String pos) {
        String converted = "";
        if (pos.equals("n")) {
            converted = "NN";
        } else if (pos.equals("v")) {
            converted = "VB";
        } else if (pos.equals("a")) {
            converted = "JJ";
        } else if (pos.equals("r")) {
            converted = "RB";
        }
        return converted;
    }

}
