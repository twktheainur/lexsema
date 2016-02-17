package org.getalp.lexsema.acceptali.closure.generator;

import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosureImpl;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.ontolex.dbnary.DBNary;

import java.util.Set;

public class CompositeLexicalResourceTranslationClosure implements TranslationClosureGenerator {

    private final DBNary dbNary;
    private final LexicalEntry lexicalEntry;

    public CompositeLexicalResourceTranslationClosure(final DBNary dbNary, final LexicalEntry lexicalEntry) {
        this.dbNary = dbNary;
        this.lexicalEntry = lexicalEntry;
    }

    @Override
    public LexicalResourceTranslationClosure<LexicalSense> generateClosure(int degree) {
        TranslationClosureGenerator translationClosureGenerator = TranslationClosureGeneratorSingle.createTranslationClosureGenerator(dbNary, lexicalEntry);
        LexicalResourceTranslationClosure<LexicalSense> initialClosure = generateClosure(translationClosureGenerator, degree);
        return generateSecondaryClosures(initialClosure, degree);
    }

    @Override
    public LexicalResourceTranslationClosure<LexicalSense> generateClosure() {
        return generateClosure(1);
    }

    private LexicalResourceTranslationClosure<LexicalSense> generateClosure(TranslationClosureGenerator translationClosureGenerator, int degree) {
        return translationClosureGenerator.generateClosure(degree);
    }

    private LexicalResourceTranslationClosure<LexicalSense> generateSecondaryClosures(LexicalResourceTranslationClosure<LexicalSense> initialClosure, int degree) {
        LexicalResourceTranslationClosure<LexicalSense> secondaryClosure = new LexicalResourceTranslationClosureImpl();
        Set<LexicalEntry> entryClosureSet = initialClosure.entryFlatClosure();
        for (LexicalEntry localLexicalEntry : entryClosureSet) {
            TranslationClosureGenerator translationClosureGenerator = TranslationClosureGeneratorSingle.createTranslationClosureGenerator(dbNary, localLexicalEntry);
            generateEntrySubClosure(translationClosureGenerator, degree, secondaryClosure);
        }
        initialClosure.importClosure(secondaryClosure);
        return secondaryClosure;
    }

    private void generateEntrySubClosure(TranslationClosureGenerator translationClosureGenerator, int degree, LexicalResourceTranslationClosure<LexicalSense> secondaryClosure) {
        secondaryClosure.importClosure(generateClosure(translationClosureGenerator, degree));
    }
}
