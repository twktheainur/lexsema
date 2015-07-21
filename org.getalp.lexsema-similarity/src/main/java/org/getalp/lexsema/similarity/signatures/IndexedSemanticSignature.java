package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.signatures.symbols.IndexedSemanticSymbol;

import java.util.List;


public interface IndexedSemanticSignature extends SemanticSignature{
    List<Integer> getIndexedSymbols();
    IndexedSemanticSymbol getIndexedSymbol(int index);
    void addIndexedSymbol(Integer symbol);
    void sort();
}
