package org.getalp.lexsema.similarity.signatures;


public interface SemanticSymbol extends Comparable<SemanticSymbol> {
    String getSymbol();
    Double getWeight();
}
