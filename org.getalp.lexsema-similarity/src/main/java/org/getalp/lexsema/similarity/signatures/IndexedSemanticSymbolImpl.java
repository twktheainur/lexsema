package org.getalp.lexsema.similarity.signatures;


public class IndexedSemanticSymbolImpl implements IndexedSemanticSymbol {
    private Integer symbol;
    private Double weight;

    public IndexedSemanticSymbolImpl(Integer symbol, double weight) {
        this.weight = weight;
        this.symbol = symbol;
    }

    @Override
    public int compareTo(IndexedSemanticSymbol o) {
        int symbolCmp = symbol.compareTo(o.getSymbol());
        if (symbolCmp == 0) {
            symbolCmp = weight.compareTo(o.getWeight());
        }
        return symbolCmp;
    }

    @Override
    public Integer getSymbol() {
        return symbol;
    }

    @Override
    public Double getWeight() {
        return weight;
    }
}
