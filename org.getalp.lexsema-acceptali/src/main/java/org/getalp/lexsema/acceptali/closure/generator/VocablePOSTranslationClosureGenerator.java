package org.getalp.lexsema.acceptali.closure.generator;

import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosureImpl;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Vocable;


class VocablePOSTranslationClosureGenerator implements TranslationClosureGenerator {
    private final Vocable vocable;
    private final String partOfSpeech;
    private final DBNary dbNary;

    VocablePOSTranslationClosureGenerator(final String partOfSpeech,
                                          final Vocable vocable,
                                          final DBNary dbNary) {
        this.partOfSpeech = partOfSpeech;
        this.dbNary = dbNary;
        this.vocable = vocable;
    }

    @Override
    public LexicalResourceTranslationClosure<LexicalSense> generateClosure(int degree) {
        LexicalResourceTranslationClosure<LexicalSense> finalClosure = new LexicalResourceTranslationClosureImpl();
        for (LexicalEntry lexicalEntry : dbNary.getLexicalEntries(vocable)) {
            if (partOfSpeech.equals(lexicalEntry.getPartOfSpeech())) {
                TranslationClosureGenerator translationClosureGenerator = TranslationClosureGeneratorFactory.createSimpleGenerator(dbNary, lexicalEntry);
                generateEntryClosure(translationClosureGenerator, finalClosure, degree);
            }
        }
        return finalClosure;
    }

    @Override
    public LexicalResourceTranslationClosure<LexicalSense> generateClosure() {
        return generateClosure(1);
    }

    void generateEntryClosure(TranslationClosureGenerator translationClosureGenerator,
                              LexicalResourceTranslationClosure<LexicalSense> finalClosure, int degree) {
        finalClosure.importClosure(translationClosureGenerator.generateClosure(degree));
    }
}
