package org.getalp.lexsema.axalign.closure.generator;

import org.getalp.lexsema.axalign.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.getalp.lexsema.similarity.Sense;

import java.util.Set;

/**
 * Wraps the LexicalSense elements in Sense instances and generating semantic signatures
 */
public interface TranslationClosureSemanticSignatureGenerator {
    Set<Sense> generateSemanticSignatures(LexicalResourceTranslationClosure<LexicalSense> rawClosure);
}
