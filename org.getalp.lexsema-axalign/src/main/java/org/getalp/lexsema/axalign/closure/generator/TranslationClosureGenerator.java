package org.getalp.lexsema.axalign.closure.generator;

import org.getalp.lexsema.axalign.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.ontolex.LexicalSense;

public interface TranslationClosureGenerator {
    LexicalResourceTranslationClosure<LexicalSense> generateClosure(int degree);

    LexicalResourceTranslationClosure<LexicalSense> generateClosure();
}
