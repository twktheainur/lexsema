package org.getalp.lexsema.supervised.features;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.supervised.features.extractors.LocalTextFeatureExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SemCorTrainingDataExtractor implements TrainingDataExtractor {

    private static final Logger logger = LoggerFactory.getLogger(SemCorTrainingDataExtractor.class);
    private final LocalTextFeatureExtractor localTextFeatureExtractor;
    private final Map<String, List<List<String>>> instanceVectors;
    private final boolean useSurfaceForm;

    public SemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor) {
        this(localTextFeatureExtractor, false);
    }

    public SemCorTrainingDataExtractor(LocalTextFeatureExtractor localTextFeatureExtractor, boolean useSurfaceForm) {
        this.useSurfaceForm = useSurfaceForm;
        this.localTextFeatureExtractor = localTextFeatureExtractor;
        instanceVectors = new HashMap<>();
    }

    @SuppressWarnings("all")
    @Override
    public void extract(Iterable<Text> annotatedCorpus) {

        logger.info("Extracting features...");

        for (Text text : annotatedCorpus) {
            final AtomicInteger wordIndex = new AtomicInteger(0);

            text.words().parallelStream().forEach(w -> {
                processWord(w, text, wordIndex.getAndIncrement());
            });/*
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }


    private void processWord(Word w, Document text, int wordIndex) {
        String semanticTag = w.getSenseAnnotation();
        if(semanticTag!=null && !semanticTag.isEmpty()) {
            String lemma = w.getLemma();
            String surfaceForm = w.getSurfaceForm();
            List<String> instance = localTextFeatureExtractor.getFeatures(text, wordIndex);

            instance.add(0, "\"" + semanticTag + "\"");
            String key;
            if (useSurfaceForm) {
                key = surfaceForm;
            } else {
                key = lemma;
            }
            synchronized (instanceVectors) {
                if (!instanceVectors.containsKey(key)) {
                    instanceVectors.put(key, new ArrayList<>());
                }
                instanceVectors.get(key).add(instance);
            }
       }
    }

    /*supprimer ?*/
    private List<String> index2Sparse(List<String> instances){

       List<String> newInstances = new ArrayList<String>(instances.size());

        for(String s : instances){

            System.out.println(s);

        }

        return newInstances;
    }

    @Override
    public List<List<String>> getWordFeaturesInstances(String lemma) {
        return instanceVectors.get(lemma);
    }

    @Override
    public List<List<String>> getSensesFeaturesInstances(List<String> senseTags) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getAttributes(String lemma) {
        List<String> attributes = new ArrayList<>();
        List<List<String>> instances = getWordFeaturesInstances(lemma);
        int size = 0;
        if (!instances.isEmpty()) {
            size = convert(instances.get(0)).size();
        }
        attributes.add("SENSE");
        for (int i = 1; i < size; i++) {
            attributes.add(String.format("C%d", i));
        }
        return Collections.unmodifiableList(attributes);
    }

    private List<String> convert(List<String> tokens) {

        List<String> ls = new ArrayList<String>(10000);//tokens.subList(0, tokens.size());//comment créer une liste ??

        ls.add(tokens.get(0));//identifiant sens

        char state = 'S';
        int vocabularySize=-1;
        int ivoc = 0;//iterateur sur vocabulaire

        for(int i = 1 ; i < tokens.size(); i++){


            String current = tokens.get(i);

            switch(state){

                case 'S':{//cas par défaut, on recopie le vecteur jusqu'à arriver à un debVector

                    if(current.equals("debVector"))
                        state = 'A';
                    else{

                        ls.add(current);
                    }
                    break;
                }

                case 'A':{//lecture taille vecteur

                    vocabularySize=Integer.parseInt(current);
                    state = 'B';
                    break;
                }

                case 'B':{

                    if(current.equals("endVector")) {

                        //compléter le vecteur de vocabulaire par des 0
                        for (;ivoc < vocabularySize; ivoc++){

                            ls.add("0");
                        }
                        state = 'S';
                    }
                    else{//mettre des zeros dans le vecteur du vocabulaire jusqu'à l'indice et y mettre un 1

                        int nextValue = Integer.parseInt(current);
                        for (;ivoc < nextValue; ivoc++){

                            ls.add("0");
                        }
                        ls.add("1");
                        ivoc++;
                    }

                    break;
                }

                default:{

                    System.err.println("ERROR");
                    System.exit(0);
                    break;
                }


            }

        }
        return ls;
    }
}
