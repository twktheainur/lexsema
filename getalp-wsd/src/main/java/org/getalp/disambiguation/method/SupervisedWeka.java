package org.getalp.disambiguation.method;

import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.weka.FeatureIndex;
import org.getalp.disambiguation.method.weka.WekaClassifier;
import org.getalp.disambiguation.method.weka.WekaClassifierSetUp;
import org.getalp.io.Document;
import org.getalp.io.Sense;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tchechem on 10/14/14.
 */
public class SupervisedWeka implements Disambiguator {

    private int lemmamin;
    private int lemmamax;
    private int posmin;
    private int posmax;
    private String dataPath;
    private Map<String, WekaClassifier> classifiers;
    private FeatureIndex featureIndex;
    private WekaClassifierSetUp classifierSetUp;

    public SupervisedWeka(int lemmamin, int lemmamax, int posmin, int posmax, String dataPath, WekaClassifierSetUp classifierSetUp) {
        this.lemmamin = lemmamin;
        this.lemmamax = lemmamax;
        this.posmin = posmin;
        this.posmax = posmax;
        this.dataPath = dataPath;
        classifiers = new HashMap<String, WekaClassifier>();
        featureIndex = new FeatureIndex();
        this.classifierSetUp = classifierSetUp;
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

    @Override
    public Configuration disambiguate(Document document) {
        return disambiguate(document, null);
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {
        boolean progressChecked = false;
        if (c == null) {
            c = new Configuration(document);
        }
        for (int i = 0; i < document.getLexicalEntries().size(); i++) {
            System.err.print(String.format("\tDisambiguating: %.2f%%\r", ((double) i / (double) document.getLexicalEntries().size()) * 100d));

            String targetLemma = document.getLexicalEntries().get(i).getLemma();
            String targetPos = convertPos(document.getLexicalEntries().get(i).getPos());

            List<String> features = new ArrayList<String>();
            for (int j = i - lemmamin; j <= i + lemmamax; j++) {
                if (i != j) {
                    String lemmaFeature;
                    if (j < 0 || j >= document.getLexicalEntries().size()) {
                        lemmaFeature = "\"X\"";
                    } else {
                        lemmaFeature = "\"" + document.getLexicalEntries().get(j).getLemma() + "\"";
                    }
                    features.add(lemmaFeature);
                }
            }

            for (int j = i - posmin; j <= i + posmax; j++) {
                if (j != i) {
                    String posFeature;
                    if (j < 0 || j >= document.getLexicalEntries().size()) {
                        posFeature = "\"0\"";
                    } else {
                        posFeature = "\"" + convertPos(document.getLexicalEntries().get(j).getPos()) + "\"";
                    }
                    features.add(posFeature);
                }
            }
            features.add("\"" + targetLemma + "\"");
            features.add("\"" + targetPos + "\"");


            List<WekaClassifier.ClassificationEntry> results = runClassifier(targetLemma, features);
            if (results.size() == 0) {
                if (document.getSenses().get(i).size() == 1) {
                    c.setSense(i, 0);
                } else {
                    c.setSense(i, -1);
                }
            } else {
                c.setSense(i, -1);
                int s = -1;
                for (int re = 0; re < results.size() && (s = getMatchingSense(document, results.get(re).getKey(), i)) == -1; re++)
                    ;
                c.setSense(i, s);
            }
        }
        return c;
    }

    @Override
    public void release() {

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
}
