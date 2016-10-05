package org.getalp.lexsema.axalign.closure.generator;

import org.getalp.lexsema.axalign.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.axalign.closure.LexicalResourceTranslationClosureImpl;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Translation;
import org.getalp.lexsema.ontolex.dbnary.Vocable;
import org.getalp.lexsema.ontolex.dbnary.exceptions.NoSuchVocableException;
import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TranslationClosureGeneratorSingle implements TranslationClosureGenerator {
    private static final Pattern TONIC_ACCENT_PATTERN = Pattern.compile("́", Pattern.LITERAL);
    private final DBNary dbNary;
    private final LexicalEntry lexicalEntry;

    private boolean includeStartingSet;

    private TranslationClosureGeneratorSingle(final DBNary dbNary, final LexicalEntry lexicalEntry) {
        this.dbNary = dbNary;
        this.lexicalEntry = lexicalEntry;
    }

    static TranslationClosureGenerator createTranslationClosureGenerator(final DBNary dbNary, final LexicalEntry lexicalEntry) {
        return new TranslationClosureGeneratorSingle(dbNary, lexicalEntry).includeStartingSet(true);
    }

    @Override
    public LexicalResourceTranslationClosure<LexicalSense> generateClosure(int degree) {
        return recurseClosure(lexicalEntry, lexicalEntry.getLanguage(), degree, true);
    }

    @Override
    public LexicalResourceTranslationClosure<LexicalSense> generateClosure() {
        return generateClosure(1);
    }

    @SuppressWarnings("all")
    private LexicalResourceTranslationClosure<LexicalSense> recurseClosure(LexicalEntry entity, Language startingLanguage, int degree, boolean topLevel) {
        LexicalResourceTranslationClosure<LexicalSense> closure = new LexicalResourceTranslationClosureImpl();
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
                        LexicalResourceTranslationClosure<LexicalSense> localClosure;
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
        if (lang != null && lang != startingLanguage) {
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
                writtenForm = TONIC_ACCENT_PATTERN.matcher(writtenForm).replaceAll(Matcher.quoteReplacement(""));
                Vocable tv = dbNary.getVocable(writtenForm, lang);

                return dbNary.getLexicalEntries(tv);
            } catch (NoSuchVocableException ignored) {
            }
        }
        return new ArrayList<>();
    }


    @SuppressWarnings({"PublicMethodNotExposedInInterface", "BooleanParameter"})
    private TranslationClosureGenerator includeStartingSet(boolean includeStartingSet) {
        this.includeStartingSet = includeStartingSet;
        return this;
    }
}
