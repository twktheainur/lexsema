package org.getalp.lexsema.acceptali.closure.generator;

import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.ontolex.LexicalSense;

public interface TranslationClosureGenerator {
    LexicalResourceTranslationClosure<LexicalSense> generateClosure(int degree);

    LexicalResourceTranslationClosure<LexicalSense> generateClosure();
}
