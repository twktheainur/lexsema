package org.getalp.lexsema.similarity.signatures;

import java.util.List;


public interface IndexedSemanticSignature extends SemanticSignature, Iterable<IndexedSemanticSymbol> {
    /**
     * Adds a weighted symbol to the semantic signature
     *
     * @param symbol The string representative of the symbol
     * @param weight The weight associated to the symbol
     */
    void addSymbol(Integer symbol, double weight);

    /**
     * Adds a symbol to the signature
     *
     * @param symbol The string representative of the symbol
     */
    void addSymbol(Integer symbol);


    /**
     * Add a string of symbols
     *
     * @param symbols The symbol string
     */
    void addSymbols(List<IndexedSemanticSymbol> symbols);

    /**
     * Add a string of symbols and their associated weights as a collection
     *
     * @param string  The symbol string
     * @param weights The respective weights of the symbols
     */
    void addSymbolString(List<Integer> string, List<Double> weights);

    void addSymbolString(List<Integer> string);

    IndexedSemanticSignature appendSignature(IndexedSemanticSignature other);

    /**
     * Merge the present semantic signature with <code>other</code> and returns a copy of the
     * annotresult
     *
     * @param other The other semantic signature to merge with the current one
     * @return The copy of the merged signature
     */
    IndexedSemanticSignature mergeSignatures(IndexedSemanticSignature other);

    /**
     * Add a semantic symbol represented by <code>symbol</code>
     *
     * @param symbol the semantic symbol
     */
    void addSymbol(IndexedSemanticSymbol symbol);

    IndexedSemanticSignature copy();

    List<Integer> getSymbols();

    public IndexedSemanticSymbol getSymbol(int index);
    
    public void sort();
}
