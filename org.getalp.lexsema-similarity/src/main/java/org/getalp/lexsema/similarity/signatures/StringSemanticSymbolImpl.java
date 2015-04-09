package org.getalp.lexsema.similarity.signatures;

import lombok.Data;

@Data
public class StringSemanticSymbolImpl implements StringSemanticSymbol {
    private String symbol;
    private Double weight;

    public StringSemanticSymbolImpl(String symbol, double weight) {
        this.weight = weight;
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public int compareTo(StringSemanticSymbol o) {
        int symbolCmp = symbol.compareTo(o.getSymbol());
        if (symbolCmp == 0) {
            symbolCmp = weight.compareTo(o.getWeight());
        }
        return symbolCmp;
    }

}
