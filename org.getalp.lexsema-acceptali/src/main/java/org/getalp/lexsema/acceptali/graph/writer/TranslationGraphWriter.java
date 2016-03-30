package org.getalp.lexsema.acceptali.graph.writer;

import org.getalp.lexsema.acceptali.closure.LexicalResourceTranslationClosure;
import org.getalp.lexsema.ontolex.LexicalEntry;
import org.getalp.lexsema.ontolex.LexicalSense;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public interface TranslationGraphWriter {
    public void writeTranslation(Graph<LexicalEntry,DefaultEdge> g);
}