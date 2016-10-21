package org.getalp.lexsema.similarity.signatures.symbols;

public class DefaultSemanticSymbolFactory implements SemanticSymbolFactory {

    public static final SemanticSymbolFactory DEFAULT_FACTORY = new DefaultSemanticSymbolFactory();

    @Override
    public VectorizedSemanticSymbol createVectorizedSemanticSymbol(double... vector) {
        return createVectorizedSemanticSymbol(vector, 1.0d);
    }

    @Override
    public VectorizedSemanticSymbol createVectorizedSemanticSymbol(double[] vector, double weight) {
        return new VectorizedSemanticSymbolImpl(vector, weight);
    }

    @Override
    public SemanticSymbol createSemanticSymbol(String symbol) {
        return createSemanticSymbol(symbol, 1.0d);
    }

    @Override
    public SemanticSymbol createSemanticSymbol(String symbol, double weight) {
        return new SemanticSymbolImpl(symbol, weight);
    }

    @Override
    public IndexedSemanticSymbol createIndexedSemanticSymbol(int symbol) {
        return createIndexedSemanticSymbol(symbol, 1.0d);
    }
    @Override
    public IndexedSemanticSymbol createIndexedSemanticSymbol(int symbol, double weight) {
        return new IndexedSemanticSymbolImpl(symbol,weight);
    }

}
