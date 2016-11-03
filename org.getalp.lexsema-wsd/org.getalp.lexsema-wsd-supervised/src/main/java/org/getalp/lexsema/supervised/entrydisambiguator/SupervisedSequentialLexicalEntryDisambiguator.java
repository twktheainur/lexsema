package org.getalp.lexsema.supervised.entrydisambiguator;


import org.getalp.lexsema.ml.supervised.ClassificationOutput;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sense;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.supervised.features.TrainingDataExtractor;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators.SequentialLexicalEntryDisambiguator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;

public abstract class SupervisedSequentialLexicalEntryDisambiguator extends SequentialLexicalEntryDisambiguator {

    private static Logger logger = LoggerFactory.getLogger(SupervisedSequentialLexicalEntryDisambiguator.class);

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
            Word targetWord = getDocument().getWord(0, getCurrentIndex());
            String targetLemma = targetWord.getLemma();

            //getConfiguration().setSense(getCurrentIndex(), -1);
            if (!targetWord.getId().isEmpty()) {
                List<String> features = featureExtractor.getFeatures(getDocument(), getCurrentIndex());
                List<ClassificationOutput> results = runClassifier(targetLemma, features);
                if (!results.isEmpty()) {
                    getConfiguration().setSenseId(getCurrentIndex(),results.get(0).getKey());
//                    int s = -1;
//                    boolean matched = false;
//                    for (int re = 0; re < results.size() && !matched; re++) {
//                        logger.debug(MessageFormat.format("Checking:  {0}", results.get(re).getKey()));
//                        //System.err.println("Checking: "+ results.get(re).getKey() );
//                        s = getMatchingSense(getDocument(), results.get(re).getKey(), getCurrentIndex());
//                        logger.debug(MessageFormat.format("s:  {0}", s));
//                        if (s != -1) {
//                            matched = true;
//                            logger.debug(MessageFormat.format("Found at WN sense index: {0}", s));
//                            //  System.err.println("Found at WN sense index: "+ s);
//                        }
//                    }
//                    getConfiguration().setSense(getCurrentIndex(), s);
//                } else {
//                    //getConfiguration().setSense(getCurrentIndex(), 0);
//                    getConfiguration().setSense(getCurrentIndex(), -1);
                }
                getConfiguration().setConfidence(getCurrentIndex(), 1d);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected int getMatchingSense(Document d, String tag, int wordIndex) {

        logger.debug(MessageFormat.format("Nb Senses for {0}:  {1}", wordIndex, d.getSenses(wordIndex).size()));
        logger.debug(MessageFormat.format("Nb Senses for {0}:  {1}", wordIndex, d.getSenses(wordIndex).size()));

        for (int s = 0; s < d.getSenses(wordIndex).size(); s++) {
            Sense cs = d.getSenses(wordIndex).get(s);
            logger.debug(MessageFormat.format("cs:  {0}", d.getSenses(wordIndex).get(s)));
            logger.debug(MessageFormat.format("cs Id:  {0}", cs.getId()));
            logger.debug(MessageFormat.format("tag:  {0}", tag));
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
