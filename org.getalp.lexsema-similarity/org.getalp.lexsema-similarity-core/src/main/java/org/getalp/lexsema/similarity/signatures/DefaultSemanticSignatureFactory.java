package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.symbols.IndexedSemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.VectorizedSemanticSymbol;

import java.util.List;

public class DefaultSemanticSignatureFactory implements SemanticSignatureFactory {

    public static final SemanticSignatureFactory DEFAULT = new DefaultSemanticSignatureFactory();

    @Override
    public SemanticSignature createSemanticSignature() {
        return new SemanticSignatureImpl();
    }

    @Override
    public SemanticSignature createSemanticSignature(CharSequence symbolString) {
        return new SemanticSignatureImpl(symbolString);
    }

    @Override
    public SemanticSignature createSemanticSignature(List<SemanticSymbol> symbols) {
        return new SemanticSignatureImpl(symbols);
    }

    @Override
    public SemanticSignature createNullSemanticSignature() {
        return NullSemanticSignature.getInstance();
    }

    @Override
    public IndexedSemanticSignature createIndexedSemanticSignature() {
        return new IndexedSemanticSignatureImpl();
    }

    @Override
    public IndexedSemanticSignature createIndexedSemanticSignature(SymbolIndex symbolIndex) {
        return new IndexedSemanticSignatureImpl(symbolIndex);
    }

    @Override
    public IndexedSemanticSignature createIndexedSemanticSignature(List<IndexedSemanticSymbol> symbols, SymbolIndex symbolIndex) {
        return new IndexedSemanticSignatureImpl(symbols,symbolIndex);
    }

    @Override
    public VectorizedSemanticSignature createVectorizedSemanticSignature() {
        return new VectorizedSemanticSignatureImpl();
    }

    @Override
    public VectorizedSemanticSignature createVectorizedSemanticSignature(List<VectorizedSemanticSymbol> symbols) {
        return new VectorizedSemanticSignatureImpl(symbols);
    }
}
