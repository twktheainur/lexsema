package org.getalp.lexsema.acceptali;

import org.getalp.lexsema.language.Language;
import org.getalp.lexsema.ontolex.LexicalEntry;
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
    private Map<Language, DBNary> dbnaryMap;

    public GenerateTranslationClosure(Map<Language, DBNary> dbnaryMap) {
        this.dbnaryMap = dbnaryMap;
    }

    public Map<Language, Map<LexicalEntry,Set<LexicalSense>>> recurseClosure(LexicalEntry entity, Language startingLanguage, int degree) {
        return recurseClosure(entity,startingLanguage,startingLanguage,degree,true);
    }

    public void showTranslations(LexicalEntry entity, Locale language) {
        Map<Locale, Map<LexicalEntry, Set<LexicalSense>>> output = new HashMap<>();
        DBNary localLanguageDBnary = dbnaryMap.get(language);
        if (localLanguageDBnary != null) {
            List<Translation> sourceTranslations = localLanguageDBnary.getTranslations(entity);
            for (Translation translation : sourceTranslations) {
                logger.info(translation.toString());
            }
        }
    }

    private Map<Language, Map<LexicalEntry,Set<LexicalSense>>> recurseClosure(LexicalEntry entity, Language language, Language startingLanguage, int degree, boolean topLevel) {
        Map<Language, Map<LexicalEntry,Set<LexicalSense>>> output = new HashMap<>();
        DBNary localLanguageDBnary = dbnaryMap.get(language);

        for(Language l : dbnaryMap.keySet()){
                output.put(l, new HashMap<LexicalEntry, Set<LexicalSense>>());
        }

        if (localLanguageDBnary != null) {

            if(!output.get(language).containsKey(entity)){
                output.get(language).put(entity,new TreeSet<LexicalSense>());
            }
            List<LexicalSense> senses = localLanguageDBnary.getLexicalSenses(entity);
            output.get(language).get(entity).addAll(senses);

            List<Translation> sourceTranslations = localLanguageDBnary.getTranslations(entity);
            String pos = entity.getPartOfSpeech();

            if (degree >=    0) {
                for (Translation translation : sourceTranslations) {
                    //The language code is the last token in the lexvo URI
                    Language lang = translation.getLanguage();
                    if(lang!=null) {
                        if (dbnaryMap.containsKey(lang) && !lang.equals(startingLanguage)) {
                            try {
                                /*
                                 * Removing language tag...
                                 */
                                String writtenForm = translation.getWrittenForm();
                                if (writtenForm.contains("@")) {
                                    writtenForm = writtenForm.split("@")[0];
                                }
                                /*
                                 * Removing tonic accent marker (Russian...)
                                 */
                                writtenForm = writtenForm.replace("́","");
                                Vocable tv = dbnaryMap.get(lang).getVocable(writtenForm);

                                List<LexicalEntry> entries = dbnaryMap.get(lang).getLexicalEntries(tv);

                                for (LexicalEntry le : entries) {
                                    if (le.getPartOfSpeech().equals(pos)) {
                                        Map<Language, Map<LexicalEntry, Set<LexicalSense>>> localClosure;
                                        if(topLevel) {

                                                    localClosure = recurseClosure(le, lang,startingLanguage, degree,false);
                                        } else {
                                            localClosure = recurseClosure(le, lang,startingLanguage, degree - 1, false);
                                        }
                                        for (Language l : localClosure.keySet()) {
                                            for(LexicalEntry localle: localClosure.get(l).keySet()) {
                                                if(!output.get(l).containsKey(localle)){
                                                    output.get(l).put(localle,new TreeSet<LexicalSense>());
                                                }
                                                output.get(l).get(localle).addAll(localClosure.get(l).get(localle));
                                            }
                                        }
                                    }
                                }
                            } catch (NoSuchVocableException e) {
                                logger.warn(e.getLocalizedMessage());
                            }
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
