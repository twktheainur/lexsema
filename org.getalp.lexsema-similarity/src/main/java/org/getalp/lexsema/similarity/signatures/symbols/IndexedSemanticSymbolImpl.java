package org.getalp.lexsema.similarity.signatures.symbols;


public class IndexedSemanticSymbolImpl implements IndexedSemanticSymbol {
    private final Integer symbol;
    private final double weight;

    public IndexedSemanticSymbolImpl(Integer symbol, double weight) {
        this.weight = weight;
        this.symbol = symbol;
    }

    @Override
    public int compareTo(SemanticSymbol o) {
        int symbolCmp = 0;
        if (o instanceof IndexedSemanticSymbol) {
            symbolCmp = symbol.compareTo(((IndexedSemanticSymbol) o).getIndexedSymbol());
        } else {
            symbolCmp = getSymbol().compareTo(o.getSymbol());
        }
        if (symbolCmp == 0) {
            return Double.valueOf(weight).compareTo(o.getWeight());
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
    public double getWeight() {
        return weight;
    }
}
