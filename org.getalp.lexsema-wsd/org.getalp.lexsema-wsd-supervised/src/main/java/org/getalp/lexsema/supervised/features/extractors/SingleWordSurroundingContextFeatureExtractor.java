package org.getalp.lexsema.supervised.features.extractors;

import org.getalp.lexsema.io.document.loader.CorpusLoader;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SingleWordSurroundingContextFeatureExtractor implements LocalTextFeatureExtractor {

    private final int lemmamin;
    private final int lemmamax;
    private static HashMap<String, Integer> index;
    private static Logger logger = LoggerFactory.getLogger(SingleWordSurroundingContextFeatureExtractor.class);
    private static int nbDone = 0;

    public SingleWordSurroundingContextFeatureExtractor(int lemmaMin, int lemmaMax) {
        lemmamin = lemmaMin;
        lemmamax = lemmaMax;
    }

    public static void buildIndex(List<Text> cl) {

        logger.debug("Entrée de buildIndex : " + cl);

        index = new HashMap<String, Integer>(1000000, 1000000);
        int value = 0;
        String lemma;

        for (Document d : cl) {

            for (int i = 0; i < d.size(); i++) {

                lemma = d.getWord(i).getLemma();

                if (!lemma.equals("") && index.get(lemma) == null) {

                    logger.debug("lemma " + lemma);
                    index.put(lemma, new Integer(value++));
                }
            }
            logger.debug("taille de d " + d.size());
            logger.debug("taille de index " + index.size());
            logger.debug("taille de value " + value);
        }
        logger.debug(" ");
        logger.debug("taille de index " + index.size());
        logger.debug("taille de value " + value);
    }

    @Override
    public synchronized List<String> getFeatures(Document d, int currentIndex) {

        if (index == null) {

            logger.debug("Index is empty, please build it with static method buildIndex");
            System.exit(0);

        }

        List<String> features = new ArrayList<>(index.size());

        int[] feats = new int[index.size()];

        for (int i = 0; i < feats.length; i++) {

            feats[i] = 0;
        }

        for (int i = Math.max(0, currentIndex - lemmamin); i <= currentIndex + lemmamax && i < d.size(); i++) {

            logger.debug("j = " + i);
            if (i != currentIndex) {
                logger.debug("\t###########");
                logger.debug("index.get(d.getWord(0, " + i + "))");
                logger.debug("#################");
                logger.debug("d = " + d);
                Word w = d.getWord(i);
                logger.debug("w = " + w);
                logger.debug("lemma = \"" + w.getLemma() + "\"");

                int value = 0;

                if(!w.getLemma().equals("") && index.containsKey(w.getLemma())){

                    value = index.get(w.getLemma());
                    logger.debug("GET " + value);
                    feats[value]++;
                } else {

                    logger.debug("Non présent \"" + w.getLemma()+"\"");
                    //System.exit(0);
                    //  e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < feats.length; i++) {

            features.add("\"" + feats[i] + "\"");
        }

        logger.debug(features.toString());
        if (++nbDone % 1000 == 0)
            logger.info("" + nbDone);
        //System.exit(0);
        return features;
    }
}
