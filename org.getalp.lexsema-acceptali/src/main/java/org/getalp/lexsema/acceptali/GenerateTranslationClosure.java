package org.getalp.lexsema.acceptali;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalResourceEntity;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GenerateTranslationClosure {
    private static Logger logger = LoggerFactory.getLogger(GenerateTranslationClosure.class);
    private Map<Locale, DBNary> dbnaryMap;

    public GenerateTranslationClosure(Map<Locale, DBNary> dbnaryMap) {
        this.dbnaryMap = dbnaryMap;
    }

    public Map<Locale, Set<LexicalResourceEntity>> generateClosure(LexicalEntry entity, Locale language, int degree) {
        Map<Locale, Set<LexicalResourceEntity>> output = new HashMap<>();
        DBNary localLanguageDBnary = dbnaryMap.get(language);
        if (localLanguageDBnary != null) {

            List<LexicalSense> senses = localLanguageDBnary.getLexicalSenses(entity);
            if (!output.containsKey(language)) {
                output.put(language, new TreeSet<LexicalResourceEntity>());
            }
            output.get(language).addAll(senses);

            List<Translation> sourceTranslations = localLanguageDBnary.getTranslations(entity);
            String pos = entity.getPartOfSpeech();

            if (degree > 0) {
                for (Translation translation : sourceTranslations) {
                    //The language code is the last token in the lexvo URI
                    String languageCodeURI[] = translation.getLanguage().split("/");
                    String languageCode = languageCodeURI[languageCodeURI.length - 1];

                    Locale lang = Locale.forLanguageTag(languageCode);
                    if (dbnaryMap.containsKey(lang)) {
                        try {
                            String writtenForm = translation.getWrittenForm();
                            if (writtenForm.contains("@")) {
                                writtenForm = writtenForm.split("@")[0];
                            }
                            Vocable tv = dbnaryMap.get(lang).getVocable(writtenForm);

                            List<LexicalEntry> entries = dbnaryMap.get(lang).getLexicalEntries(tv);

                            if (!output.containsKey(lang)) {
                                output.put(lang, new TreeSet<LexicalResourceEntity>());
                            }

                            for (LexicalEntry le : entries) {
                                if (le.getPartOfSpeech().equals(pos)) {
                                    Map<Locale, Set<LexicalResourceEntity>> localClosure = generateClosure(le, Locale.forLanguageTag(languageCode), degree - 1);
                                    for (Locale l : localClosure.keySet()) {
                                        output.get(l).addAll(localClosure.get(l));
                                    }
                                }
                            }
                        } catch (NoSuchVocableException e) {
                            logger.error(e.getLocalizedMessage());
                        }
                    }
                }
            }
        } else {
            logger.warn("The ontology does not contain a graph for "+language);
        }
        return output;
    }

}
