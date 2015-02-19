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

import java.util.ArrayList;
import java.util.List;

public class TranslationClosureGenerator {
    private static Logger logger = LoggerFactory.getLogger(TranslationClosureGenerator.class);
    DBNary dbNary;

    private boolean includeStartingSet;

    private TranslationClosureGenerator(DBNary dbNary) {
        this.dbNary = dbNary;
    }

    public static TranslationClosureGenerator createTranslationClosureGeneratorWithoutStartingSet(DBNary dbNary) {
        return new TranslationClosureGenerator(dbNary);
    }

    public static TranslationClosureGenerator createTranslationClosureGenerator(DBNary dbNary) {
        return new TranslationClosureGenerator(dbNary).includeStartingSet(true);
    }

    public LexicalResourceTranslationClosure recurseClosure(LexicalEntry entity, int degree) {
        return recurseClosure(entity, entity.getLanguage(), degree, true);
    }

    private LexicalResourceTranslationClosure recurseClosure(LexicalEntry entity, Language startingLanguage, int degree, boolean topLevel) {
        LexicalResourceTranslationClosure closure = new LexicalResourceTranslationClosureImpl();
        if (degree >= 0) {
            Language language = entity.getLanguage();
            if (includeStartingSet || !includeStartingSet && !language.equals(startingLanguage)) {
                List<LexicalSense> senses = dbNary.getLexicalSenses(entity);
                closure.addSenses(language, entity, senses);
            }
            List<Translation> sourceTranslations = dbNary.getTranslations(entity, language);
            String pos = entity.getPartOfSpeech();
            for (Translation translation : sourceTranslations) {
                List<LexicalEntry> entries = getTargetEntries(translation, startingLanguage);
                for (LexicalEntry le : entries) {
                    if (le.getPartOfSpeech().equals(pos)) {
                        LexicalResourceTranslationClosure localClosure;
                        if (topLevel && includeStartingSet) {
                            localClosure = recurseClosure(le, startingLanguage, degree, false);
                        } else {
                            localClosure = recurseClosure(le, startingLanguage, degree - 1, false);
                        }
                        closure.importClosure(localClosure);
                    }
                }
            }
        }
        return closure;
    }

    private List<LexicalEntry> getTargetEntries(Translation translation, Language startingLanguage) {
        Language lang = translation.getLanguage();
        if (lang != null && !lang.equals(startingLanguage)) {
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
                writtenForm = writtenForm.replace("́", "");
                Vocable tv = dbNary.getVocable(writtenForm, lang);

                return dbNary.getLexicalEntries(tv);
            } catch (NoSuchVocableException ignored) {
            }
        }
        return new ArrayList<>();
    }


    public TranslationClosureGenerator includeStartingSet(boolean includeStartingSet) {
        this.includeStartingSet = includeStartingSet;
        return this;
    }
}
