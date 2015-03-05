package org.getalp.lexsema.acceptali.closure.generator;

import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.similarity.Sense;

/**
 * Wraps the LexicalSense elements in Sense instances and generating semantic signatures
 */
public interface TranslationClosureSemanticSignatureGenerator {
    LexicalResourceTranslationClosure<Sense> generateSemanticSignatures(LexicalResourceTranslationClosure<LexicalSense> rawClosure);
}
