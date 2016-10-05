package org.getalp.lexsema.similarity.signatures.symbols;


import java.io.Serializable;

public interface SemanticSymbol extends Comparable<SemanticSymbol>, Serializable {
    String getSymbol();
    double getWeight();
}
