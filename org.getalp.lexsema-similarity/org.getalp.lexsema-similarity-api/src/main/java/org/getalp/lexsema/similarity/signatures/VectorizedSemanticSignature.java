package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.signatures.symbols.VectorizedSemanticSymbol;

import java.util.List;


public interface VectorizedSemanticSignature extends SemanticSignature{
    VectorizedSemanticSignature copy();
    List<VectorizedSemanticSymbol> getVectorizedSymbols();
}
