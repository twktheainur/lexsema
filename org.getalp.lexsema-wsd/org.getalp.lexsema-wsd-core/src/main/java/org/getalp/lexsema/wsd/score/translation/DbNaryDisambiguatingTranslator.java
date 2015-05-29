package org.getalp.lexsema.wsd.score.translation;


import org.getalp.lexsema.io.resource.LRLoader;
import org.getalp.lexsema.io.resource.dbnary.DBNaryLoaderImpl;
import org.getalp.lexsema.io.text.SentenceProcessor;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.measures.xlingual.translation.Translator;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.method.Disambiguator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tartarus.snowball.SnowballStemmer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DbNaryDisambiguatingTranslator implements Translator {

    private static Logger logger = LoggerFactory.getLogger(DbNaryDisambiguatingTranslator.class);
    private final Disambiguator disambiguator;
    private final SnowballStemmer snowballStemmer;
    private final Collection<String> sourceStopList;
    private final Collection<String> targetStopList;
    private DBNary dbNary;
    private SentenceProcessor sentenceProcessor;

    public DbNaryDisambiguatingTranslator(DBNary dbNary, SentenceProcessor sentenceProcessor, Disambiguator disambiguator, SnowballStemmer snowballStemmer, Collection<String> sourceStopList, Collection<String> targetStopList) {
        this.dbNary = dbNary;
        this.sentenceProcessor = sentenceProcessor;
        this.disambiguator = disambiguator;
        this.snowballStemmer = snowballStemmer;
        this.sourceStopList = Collections.unmodifiableCollection(sourceStopList);
        this.targetStopList = targetStopList;
    }

    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {
        StringBuilder outputBuilder = new StringBuilder();
        LRLoader lrLoader = null;
        try {
            lrLoader = new DBNaryLoaderImpl(dbNary, sourceLanguage).loadDefinitions(true);
            Sentence sentence = sentenceProcessor.process(filterInput(source), "");
            loadSenses(lrLoader, sentence);
            Configuration result = disambiguator.disambiguate(sentence);
            //noinspection LawOfDemeter
            for (int i = 0; i < result.size(); i++) {
                outputBuilder.append(String.format("%s ", getWordTranslation(i, result, sentence, sourceLanguage, targetLanguage)));
            }
        } catch (IOException e) {
            logger.error("IO " + e.getLocalizedMessage());
        } catch (InvocationTargetException e) {
            logger.error("Invoke " + e.getLocalizedMessage());
        } catch (NoSuchMethodException e) {
            logger.error(e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            logger.error("Class not found: " + e.getLocalizedMessage());
        } catch (InstantiationException e) {
            logger.error("Cannot instantiate" + e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            logger.error("Illegal access" + e.getLocalizedMessage());
        }
        return outputBuilder.toString();
    }

    private String filterInput(String input) {
        return input.replaceAll("\\p{Punct}", " ");
    }

    @Override
    public void close() {
    }

    private void loadSenses(LRLoader lrLoader, Document document) {
        lrLoader.loadSenses(document);
    }

    private String getWordTranslation(int index, Configuration c, Document d, Language sourceLanguage, Language targetLanguage) {
        StringBuilder outputBuilder = new StringBuilder();
        int selectedSense = c.getAssignment(index);
        List<Translation> translations = null;
        Collection<String> uniqueTranslations = new TreeSet<>();
        String lemma = d.getWord(0, index).getLemma();
        if (selectedSense >= 0 && !targetStopList.contains(lemma)) {
            LexicalSense sense = getAssignedSense(d, index, selectedSense);
            if (sense != null) {
                translations = getDBNarySenseTranslation(sense, targetLanguage);
            }
        }
        if (translations == null || translations.isEmpty()) {
            try {
                Vocable v = dbNary.getVocable(getWordLemma(d.getWord(0, index)), sourceLanguage);
                translations = getDBNaryTranslation(v, targetLanguage);
            } catch (NoSuchVocableException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        if (translations != null) {
            for (Translation translation : translations) {
                if (translation.getLanguage().equals(targetLanguage)) {
                    uniqueTranslations.add(translation.getWrittenForm());
                }
            }
            for (String t : uniqueTranslations) {
                if (!targetStopList.contains(t)) {
                    //snowballStemmer.setCurrent(t);
                    //outputBuilder.append(snowballStemmer.stem()).append(" ");
                    outputBuilder.append(t).append(" ");
                }
            }
        }
        return outputBuilder.toString();
    }

    private String getWordLemma(LexicalEntry w) {
        return w.getLemma();
    }

    private List<Translation> getDBNaryTranslation(Vocable v, Language targetLanguage) {
        List<Translation> translations = new ArrayList<>();
        List<LexicalEntry> lexicalEntries = dbNary.getLexicalEntries(v);
        for (LexicalEntry lexicalEntry : lexicalEntries) {
            translations.addAll(dbNary.getTranslations(lexicalEntry, targetLanguage));
        }
        return translations;
    }

    private List<Translation> getDBNarySenseTranslation(LexicalResourceEntity sense, Language targetLanguage) {
        List<Translation> translations = new ArrayList<>();
        translations.addAll(dbNary.getTranslations(sense, targetLanguage));
        return translations;
    }

    private LexicalSense getAssignedSense(Document d, int index, int senseIndex) {
        if (d.getSenses(index).size() > index) {
            return d.getSenses(index).get(senseIndex);
        } else {
            return null;
        }
    }

}
