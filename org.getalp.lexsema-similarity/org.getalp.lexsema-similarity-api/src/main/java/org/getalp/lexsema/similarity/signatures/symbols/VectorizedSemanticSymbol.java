package org.getalp.lexsema.similarity.signatures.symbols;


public interface VectorizedSemanticSymbol extends SemanticSymbol{
    double[] getVector();
}
