package org.getalp.lexsema.acceptali.crosslingual.translation;

import org.getalp.lexsema.io.text.SentenceProcessor;
import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.similarity.Sentence;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.util.segmentation.Segmenter;
import org.getalp.lexsema.util.segmentation.SpaceSegmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DbNaryTranslator implements Translator {

    private static Logger logger = LoggerFactory.getLogger(DbNaryTranslator.class);
    private DBNary dbNary;
    private SentenceProcessor sentenceProcessor;

    public DbNaryTranslator(DBNary dbNary, SentenceProcessor sentenceProcessor) {
        this.dbNary = dbNary;
        this.sentenceProcessor = sentenceProcessor;
    }

    public DbNaryTranslator(DBNary dbNary) {
        this.dbNary = dbNary;
    }

    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {
        StringBuilder outputBuilder = new StringBuilder();

        if (sentenceProcessor != null) {
            Sentence sentence = sentenceProcessor.process(source, "", sourceLanguage.getLanguageName());
            for (Word w : sentence) {
                outputBuilder.append(String.format("%s ", getWordTranslation(w.getLemma(), sourceLanguage, targetLanguage)));
            }
        } else {
            Segmenter s = new SpaceSegmenter();
            List<String> sentence = s.segment(source.replaceAll("\\p{Punct}", " ").replaceAll("  ", " "));
            for (String w : sentence) {
                outputBuilder.append(String.format("%s ", getWordTranslation(w, sourceLanguage, targetLanguage)));
            }
        }
        return outputBuilder.toString();
    }

    private String getWordTranslation(String form, Language sourceLanguage, Language targetLanguage) {
        StringBuilder outputBuilder = new StringBuilder();
        try {
            Vocable v = dbNary.getVocable(form, sourceLanguage);
            List<Translation> translations = getDBNaryTranslation(v, targetLanguage);
            for (Translation translation : translations) {
                outputBuilder.append(String.format("%s ", translation.getWrittenForm()));
            }
            return outputBuilder.toString();
        } catch (NoSuchVocableException ignored) {
            //logger.error(e.getLocalizedMessage());
        }
        return "";
    }

    private List<Translation> getDBNaryTranslation(Vocable v, Language targetLanguage) {
        List<Translation> translations = new ArrayList<>();
        List<LexicalEntry> lexicalEntries = dbNary.getLexicalEntries(v);
        for (LexicalEntry lexicalEntry : lexicalEntries) {
            translations.addAll(dbNary.getTranslations(lexicalEntry, targetLanguage));
        }
        return translations;
    }

}
