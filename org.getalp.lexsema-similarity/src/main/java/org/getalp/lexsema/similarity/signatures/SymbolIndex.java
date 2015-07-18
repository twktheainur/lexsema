package org.getalp.lexsema.similarity.signatures;

/**
 * The index holds a map of unique (Index, Symbol) pairs, that associate a unique index to each unique symbol
 */
public interface SymbolIndex {
    /**
     * Return the index for the specified symbol. If the symbol was not present in
     * the index map, it is added and a new index is assigned sequentially to the
     * last symbol added.
     * @param symbol The symbol for which we want to retrieve the index
     * @return The index of the symbol
     */
    public Integer getSymbolIndex(String symbol);
}
