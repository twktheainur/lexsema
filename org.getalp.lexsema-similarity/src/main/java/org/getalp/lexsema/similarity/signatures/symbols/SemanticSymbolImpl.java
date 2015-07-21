package org.getalp.lexsema.similarity.signatures.symbols;

import lombok.Data;

@Data
public class SemanticSymbolImpl implements SemanticSymbol {
    private final String symbol;
    private final Double weight;

    public SemanticSymbolImpl(String symbol, double weight) {
        this.weight = weight;
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(SemanticSymbol o) {
        int symbolCmp = symbol.compareTo(o.getSymbol());
        if (symbolCmp == 0) {
            symbolCmp = weight.compareTo(o.getWeight());
        }
        return symbolCmp;
    }

}
