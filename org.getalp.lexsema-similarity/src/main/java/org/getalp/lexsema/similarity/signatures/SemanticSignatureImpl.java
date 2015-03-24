package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.util.segmentation.Segmenter;
import org.getalp.lexsema.util.segmentation.SpaceSegmenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class SemanticSignatureImpl implements SemanticSignature {

    public static final double DEFAULT_WEIGHT = 1d;
    private List<SemanticSymbol> symbols;

    public SemanticSignatureImpl() {
        symbols = new ArrayList<>();
    }

    private SemanticSignatureImpl(List<SemanticSymbol> symbols) {
        this.symbols = new ArrayList<>();
        Collections.copy(symbols, this.symbols);
    }

    @Override
    public void addSymbol(String symbol, double weight) {
        symbols.add(new SemanticSymbolImpl(symbol, weight));
    }

    @Override
    public void addSymbol(String symbol) {
        addSymbol(symbol, DEFAULT_WEIGHT);
    }

    @Override
    public void addSymbols(List<SemanticSymbol> symbols) {
        for (SemanticSymbol ss : symbols) {
            addSymbol(ss);
        }
    }

    @Override
    public void addSymbolString(String string) {
        Segmenter s = new SpaceSegmenter();
        for (String tok : s.segment(string)) {
            addSymbol(tok);
        }
    }

    @Override
    public void addSymbolString(String string, Segmenter segmenter) {
        for (String tok : segmenter.segment(string)) {
            addSymbol(tok);
        }
    }

    @Override
    public void addSymbolString(List<String> string, List<Double> weights) {
        for (int i = 0; i < Math.min(string.size(), weights.size()); i++) {
            addSymbol(string.get(i), weights.get(i));
        }
    }

    @Override
    public void addSymbolString(List<String> string) {
        for (String aString : string) {
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
    public List<String> getSymbols() {
        List<String> symbols = new ArrayList<>();
        for (SemanticSymbol ss : this) {
            symbols.add(ss.getSymbol());
        }
        return symbols;
    }

    @Override
    public Iterator<SemanticSymbol> iterator() {
        return symbols.iterator();
    }

    @Override
    public SemanticSignature appendSignature(SemanticSignature other) {
        for (SemanticSymbol ss : other) {
            addSymbol(ss);
        }
        return this;
    }

    public String toString() {
        String output = "";
        for (SemanticSymbol semanticSymbol : symbols) {
            output += " " + semanticSymbol.getSymbol();
        }
        return output;
    }


    @Override
    public SemanticSignature mergeSignatures(SemanticSignature other) {
        return new SemanticSignatureImpl(symbols).appendSignature(other);
    }

    @Override
    public void addSymbol(SemanticSymbol symbol) {
        symbols.add(symbol);
    }

    @Override
    public SemanticSignature copy() {
        return new SemanticSignatureImpl(symbols);
    }

    @Override
    public int size() {
        return symbols.size();
    }
}
