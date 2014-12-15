package org.getalp.lexsema.similarity.signatures;

public interface SemanticSymbol extends Comparable<SemanticSymbol> {
    public String getSymbol();

    public Double getWeight();
}
