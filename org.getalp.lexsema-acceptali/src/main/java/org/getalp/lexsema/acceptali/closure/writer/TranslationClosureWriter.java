package org.getalp.lexsema.acceptali.closure.writer;


import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.ontolex.LexicalSense;

public interface TranslationClosureWriter {
    public void writeClosure(LexicalResourceTranslationClosure<LexicalSense> closure);
}
