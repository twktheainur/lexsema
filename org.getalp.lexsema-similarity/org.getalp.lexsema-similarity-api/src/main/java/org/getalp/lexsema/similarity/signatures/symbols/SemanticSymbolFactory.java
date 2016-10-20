package org.getalp.lexsema.similarity.signatures.symbols;

public interface SemanticSymbolFactory {

    VectorizedSemanticSymbol createVectorizedSemanticSymbol(double... vector);
    VectorizedSemanticSymbol createVectorizedSemanticSymbol(double[] vector, double weight);

    SemanticSymbol createSemanticSymbol(String symbol);
    SemanticSymbol createSemanticSymbol(String symbol, double weight);


    IndexedSemanticSymbol createIndexedSemanticSymbol(int symbol, double weight);
    IndexedSemanticSymbol createIndexedSemanticSymbol(int symbol);

}
