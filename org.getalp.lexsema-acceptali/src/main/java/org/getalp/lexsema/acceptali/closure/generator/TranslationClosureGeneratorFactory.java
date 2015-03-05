package org.getalp.lexsema.acceptali.closure.generator;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.dbnary.DBNary;
import org.getalp.lexsema.ontolex.dbnary.Vocable;

public final class TranslationClosureGeneratorFactory {

    private TranslationClosureGeneratorFactory() {
    }

    public static TranslationClosureGenerator createSimpleGenerator(DBNary dbNary, LexicalEntry entry) {
        return TranslationClosureGeneratorSingle.createTranslationClosureGenerator(dbNary, entry);
    }

    public static TranslationClosureGenerator createVocablePOSGenerator(Vocable vocable, String POS, DBNary dbNary) {
        return new VocablePOSTranslationClosureGenerator(POS, vocable, dbNary);
    }

    public static TranslationClosureGenerator createCompositeGenerator(DBNary dbNary, LexicalEntry entry) {
        return new CompositeLexicalResourceTranslationClosure(dbNary, entry);
    }

    public static TranslationClosureGenerator createFileGenerator(DBNary dbNary, String directory) {
        return new TranslationClosureGeneratorFile(dbNary, directory);
    }

}
