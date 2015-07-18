package org.getalp.lexsema.similarity.signatures;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SymbolIndexImpl implements SymbolIndex {
    private final BiMap<String, Integer> indexMap = HashBiMap.create();
    private int currentSymbol = 0;
    @Override
    public Integer getSymbolIndex(String symbol) {
        if(!indexMap.containsKey(symbol)){
            indexMap.put(symbol,currentSymbol);
            currentSymbol++;
        }
        return indexMap.get(symbol);
    }
}
