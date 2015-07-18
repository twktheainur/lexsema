package org.getalp.lexsema.similarity.signatures;


public class IndexedSemanticSymbolImpl implements IndexedSemanticSymbol {
    private final Integer symbol;
    private final Double weight;

    public IndexedSemanticSymbolImpl(Integer symbol, double weight) {
        this.weight = weight;
        this.symbol = symbol;
    }

    @Override
    public int compareTo(SemanticSymbol o) {
        final String stringSymbol = getSymbol();
        int symbolCmp = stringSymbol.compareTo(o.getSymbol());
        if (symbolCmp == 0) {
            symbolCmp = weight.compareTo(o.getWeight());
        }
        return symbolCmp;
    }

    @Override
    public Integer getIndexedSymbol() {
        return symbol;
    }

    @Override
    public String getSymbol() {
        return String.valueOf(symbol);
    }

    @Override
    public Double getWeight() {
        return weight;
    }
}
