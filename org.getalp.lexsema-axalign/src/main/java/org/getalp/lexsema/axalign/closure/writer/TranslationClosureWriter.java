package org.getalp.lexsema.axalign.closure.writer;


import org.getalp.lexsema.axalign.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.ontolex.LexicalSense;

public interface TranslationClosureWriter {
    public void writeClosure(LexicalResourceTranslationClosure<LexicalSense> closure);
}
