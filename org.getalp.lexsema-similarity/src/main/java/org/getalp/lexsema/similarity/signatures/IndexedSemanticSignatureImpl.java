package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;

import java.util.*;


public class IndexedSemanticSignatureImpl implements IndexedSemanticSignature {

    public static final double DEFAULT_WEIGHT = 1d;
    private List<IndexedSemanticSymbol> symbols;

    public IndexedSemanticSignatureImpl() {
        symbols = new ArrayList<>();
    }

    private IndexedSemanticSignatureImpl(List<IndexedSemanticSymbol> symbols) {
        this.symbols = new ArrayList<>();
        Collections.copy(symbols, this.symbols);
    }


    @Override
    public void addSymbol(Integer symbol, double weight) {
        symbols.add(new IndexedSemanticSymbolImpl(symbol, weight));
    }


    @Override
    public void addSymbol(Integer symbol) {
        addSymbol(symbol, DEFAULT_WEIGHT);
    }

    @Override
    public void addSymbols(List<IndexedSemanticSymbol> symbols) {
        for (IndexedSemanticSymbol ss : symbols) {
            addSymbol(ss);
        }
    }


    @Override
    public void addSymbolString(List<Integer> string, List<Double> weights) {
        for (int i = 0; i < Math.min(string.size(), weights.size()); i++) {
            addSymbol(string.get(i), weights.get(i));
        }
    }

    @Override
    public void addSymbolString(List<Integer> string) {
        for (Integer aString : string) {
            addSymbol(aString, 1.0);
        }
    }

    @Override
    public List<Double> getWeights() {
        List<Double> weights = new ArrayList<>();
        for (SemanticSymbol ss : this) {
            weights.add(ss.getWeight());
        }
        return weights;
    }

    @Override
    public List<Integer> getSymbols() {
        List<Integer> symbols = new ArrayList<>();
        for (IndexedSemanticSymbol ss : this) {
            symbols.add(ss.getSymbol());
        }
        return symbols;
    }

    @Override
    public Iterator<IndexedSemanticSymbol> iterator() {
        return symbols.iterator();
    }

    @Override
    public IndexedSemanticSignature appendSignature(IndexedSemanticSignature other) {
        for (IndexedSemanticSymbol ss : other) {
            addSymbol(ss);
        }
        return this;
    }

    public String toString() {
        String output = "";
        for (IndexedSemanticSymbol semanticSymbol : symbols) {
            output += String.format(" %s", semanticSymbol.getSymbol());
        }
        return output;
    }

    @Override
    public double computeSimilarityWith(SimilarityMeasure measure, SemanticSignature other,
                                        Map<String, ? extends SemanticSignature> relatedA,
                                        Map<String, ? extends SemanticSignature> relatedB) {
        if (other instanceof IndexedSemanticSignature) {
            return measure.compute(this, (IndexedSemanticSignature) other,
                    (Map<String, IndexedSemanticSignature>) relatedA, (Map<String, IndexedSemanticSignature>) relatedA);
        } else {
            return 0;
        }
    }


    @Override
    public IndexedSemanticSignature mergeSignatures(IndexedSemanticSignature other) {
        return new IndexedSemanticSignatureImpl(symbols).appendSignature(other);
    }

    @Override
    public IndexedSemanticSymbol getSymbol(int index) {
        return symbols.get(index);
    }

    @Override
    public void addSymbol(IndexedSemanticSymbol symbol) {
        symbols.add(symbol);
    }

    @Override
    public IndexedSemanticSignature copy() {
        return new IndexedSemanticSignatureImpl(symbols);
    }

    @Override
    public int size() {
        return symbols.size();
    }
}
