package org.getalp.lexsema.supervised.entrydisambiguator;


import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.supervised.ClassificationOutput;
import org.getalp.lexsema.supervised.features.TrainingDataExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;

import java.util.List;

public abstract class SupervisedSequentialLexicalEntryDisambiguator extends SequentialLexicalEntryDisambiguator {
    private LocalTextFeatureExtractor featureExtractor;
    private TrainingDataExtractor trainingDataExtractor;

    protected SupervisedSequentialLexicalEntryDisambiguator(Configuration configuration, Document d, int start, int end, int currentIndex, LocalTextFeatureExtractor featureExtractor, TrainingDataExtractor trainingDataExtractor) {
        super(configuration, d, start, end, currentIndex);
        this.featureExtractor = featureExtractor;
        this.trainingDataExtractor = trainingDataExtractor;
    }

    @Override
    public void run() {
        try {
            String targetLemma = getDocument().getWord(0, getCurrentIndex()).getLemma();

            List<String> features = featureExtractor.getFeatures(getDocument(), getCurrentIndex());

            List<ClassificationOutput> results = runClassifier(targetLemma, features);
            if (results.size() == 0) {
                getConfiguration().setSense(getCurrentIndex(), -1);
            } else {
                getConfiguration().setSense(getCurrentIndex(), -1);
                int s = -1;
                boolean matched = false;
                for (int re = 0; re < results.size() && !matched; re++) {
                    //System.err.println("Checking: "+ results.get(re).getKey() );
                    s = getMatchingSense(getDocument(), results.get(re).getKey(), getCurrentIndex());
                    if (s != -1) {
                        matched = true;
                        //  System.err.println("Found at WN sense index: "+ s);
                    }
                }
                getConfiguration().setSense(getCurrentIndex(), s);
            }
            getConfiguration().setConfidence(getCurrentIndex(), 1d);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected int getMatchingSense(Document d, String tag, int wordIndex) {
        for (int s = 0; s < d.getSenses(wordIndex).size(); s++) {
            Sense cs = d.getSenses(wordIndex).get(s);
            if (cs.getId().contains(tag.replaceAll("\"", ""))) {
                return s;
            }
        }
        return -1;
    }

    protected List<List<String>> getLemmaFeatures(String lemma) {
        return trainingDataExtractor.getWordFeaturesInstances(lemma);
    }

    protected List<String> getAttributes(String lemma) {
        return trainingDataExtractor.getAttributes(lemma);
    }


    protected abstract List<ClassificationOutput> runClassifier(String lemma, List<String> instance);
}
