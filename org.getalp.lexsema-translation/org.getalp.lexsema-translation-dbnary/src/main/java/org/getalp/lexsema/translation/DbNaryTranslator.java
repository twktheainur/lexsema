package org.getalp.lexsema.translation;

import org.getalp.lexsema.io.text.TextProcessor;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.similarity.Text;
import org.getalp.lexsema.similarity.Word;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.segmentation.Segmenter;
import org.getalp.lexsema.util.segmentation.SpaceSegmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DbNaryTranslator implements Translator {

    private static Logger logger = LoggerFactory.getLogger(DbNaryTranslator.class);
    private DBNary dbNary;
    private TextProcessor textProcessor;

    public DbNaryTranslator(DBNary dbNary, TextProcessor textProcessor) {
        this.dbNary = dbNary;
        this.textProcessor = textProcessor;
    }

    public DbNaryTranslator(DBNary dbNary) {
        this.dbNary = dbNary;
    }

    @Override
    public String translate(String source, Language sourceLanguage, Language targetLanguage) {
        StringBuilder outputBuilder = new StringBuilder();

        if (textProcessor != null) {
            Text sentence = textProcessor.process(source, "");
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

    @Override
    public void close() {

    }

    private String getWordTranslation(String form, Language sourceLanguage, Language targetLanguage) {
        StringBuilder outputBuilder = new StringBuilder();
        try {
            Vocable v = dbNary.getVocable(form, sourceLanguage);
            List<Translation> translations = getDBNaryTranslation(v, targetLanguage);
            for (Translation translation : translations) {
                if (translation.getLanguage().equals(targetLanguage)) {
                    outputBuilder.append(String.format("%s ", translation.getWrittenForm()));
                }
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
