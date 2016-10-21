package org.getalp.lexsema.similarity.signatures;


import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.symbols.IndexedSemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.VectorizedSemanticSymbol;

import java.util.List;

public interface SemanticSignatureFactory {

    SemanticSignature createSemanticSignature();
    SemanticSignature createSemanticSignature(CharSequence symbolString);
    SemanticSignature createSemanticSignature(List<SemanticSymbol> symbols);

    SemanticSignature createNullSemanticSignature();

    IndexedSemanticSignature createIndexedSemanticSignature();
    IndexedSemanticSignature createIndexedSemanticSignature(SymbolIndex symbolIndex);
    IndexedSemanticSignature createIndexedSemanticSignature(List<IndexedSemanticSymbol> symbols, SymbolIndex symbolIndex);

    VectorizedSemanticSignature createVectorizedSemanticSignature();
    VectorizedSemanticSignature createVectorizedSemanticSignature(List<VectorizedSemanticSymbol> symbols);

}
