package org.getalp.disambiguation.method.sequencial.entrydisambiguators;

import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.features.extractors.LocalTextFeatureExtractor;
import org.getalp.disambiguation.method.weka.FeatureIndex;
import org.getalp.disambiguation.method.weka.WekaClassifier;
import org.getalp.disambiguation.method.weka.WekaClassifierSetUp;
import org.getalp.io.Document;
import org.getalp.io.Sense;

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
            String targetLemma = getDocument().getLexicalEntries().get(getCurrentIndex()).getLemma();
            String targetPos = convertPos(getDocument().getLexicalEntries().get(getCurrentIndex()).getPos());

            List<String> features = featureExtractor.getFeatures(getDocument(), getCurrentIndex());

            List<WekaClassifier.ClassificationEntry> results = runClassifier(targetLemma, features);
            if (results.size() == 0) {
                getConfiguration().setSense(getCurrentIndex(), -1);
            } else {
                getConfiguration().setSense(getCurrentIndex(), -1);
                int s = -1;
                for (int re = 0; re < results.size() && (s = getMatchingSense(getDocument(), results.get(re).getKey(), getCurrentIndex())) == -1; re++)
                    ;
                getConfiguration().setSense(getCurrentIndex(), s);
            }
            getConfiguration().setConfidence(getCurrentIndex(), 1d);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public int getMatchingSense(Document d, String tag, int wordIndex) {
        for (int s = 0; s < d.getSenses().get(wordIndex).size(); s++) {
            Sense cs = d.getSenses().get(wordIndex).get(s);
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
