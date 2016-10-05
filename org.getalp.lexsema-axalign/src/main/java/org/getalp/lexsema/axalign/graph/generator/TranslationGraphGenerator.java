package org.getalp.lexsema.axalign.graph.generator;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public interface TranslationGraphGenerator {
    Graph<LexicalEntry,DefaultEdge> generateGraph(int degree);

    Graph<LexicalEntry,DefaultEdge> generateGraph();
}
