package org.getalp.lexsema.axalign.graph.tools;

import org.getalp.lexsema.ontolex.LexicalEntry;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge ;

import java.util.Set;

public class ToolGraph {

    public static Graph<LexicalEntry,DefaultEdge> importGraph(Graph<LexicalEntry,DefaultEdge> mainGraph, Graph<LexicalEntry,DefaultEdge> g){
        Set<LexicalEntry> colVertex = g.vertexSet() ;
        for(LexicalEntry le : colVertex) {
            mainGraph.addVertex(le);
        }
        Set<DefaultEdge> colEdges = g.edgeSet() ;
        for(DefaultEdge de : colEdges){
            mainGraph.addEdge(g.getEdgeSource(de),g.getEdgeTarget(de));
        }
        return mainGraph ;
    }

}
