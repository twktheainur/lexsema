package org.getalp.lexsema.similarity.signatures.symbols;


public interface SemanticSymbol extends Comparable<SemanticSymbol> {
    String getSymbol();
    double getWeight();
}
