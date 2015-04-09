package org.getalp.lexsema.similarity.signatures;

/**
 * Created by tchechem on 08/04/15.
 */
public interface IndexedSemanticSymbol extends SemanticSymbol, Comparable<IndexedSemanticSymbol> {
    Integer getSymbol();
}
