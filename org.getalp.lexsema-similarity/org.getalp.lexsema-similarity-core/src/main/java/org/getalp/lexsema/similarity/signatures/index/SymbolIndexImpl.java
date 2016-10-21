package org.getalp.lexsema.similarity.signatures.index;

import java.util.HashMap;
import java.util.Map;

public class SymbolIndexImpl implements SymbolIndex {
    
    private final Map<String, Integer> indexMap = new HashMap<>();
    
    private int currentSymbol = 0;
    
    @Override
    public Integer getSymbolIndex(String symbol) {
        if(!indexMap.containsKey(symbol)){
            indexMap.put(symbol, currentSymbol);
            currentSymbol++;
        }
        return indexMap.get(symbol);
    }
}
