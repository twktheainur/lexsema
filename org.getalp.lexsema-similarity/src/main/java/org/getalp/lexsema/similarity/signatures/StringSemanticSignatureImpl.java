package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.util.Language;

import java.util.*;


public class StringSemanticSignatureImpl implements StringSemanticSignature {

    public static final double DEFAULT_WEIGHT = 1d;
    private List<StringSemanticSymbol> symbols;
    private Language language;

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }

    public StringSemanticSignatureImpl() {
        symbols = new ArrayList<>();
    }

    private StringSemanticSignatureImpl(List<StringSemanticSymbol> symbols) {
        this.symbols = new ArrayList<>();
        Collections.copy(symbols, this.symbols);
    }


    @Override
    public void addSymbol(String symbol, double weight) {
        symbols.add(new StringSemanticSymbolImpl(symbol, weight));
    }


    @Override
    public void addSymbol(String symbol) {
        addSymbol(symbol, DEFAULT_WEIGHT);
    }

    @Override
    public void addSymbols(List<StringSemanticSymbol> symbols) {
        for (StringSemanticSymbol ss : symbols) {
            addSymbol(ss);
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
    public double computeSimilarityWith(SimilarityMeasure measure, SemanticSignature other,
                                        Map<String, ? extends SemanticSignature> relatedA,
                                        Map<String, ? extends SemanticSignature> relatedB) {
        if (other instanceof StringSemanticSignature) {
            return measure.compute(this, (StringSemanticSignature) other,
                    (Map<String, StringSemanticSignature>) relatedA,
                    (Map<String, StringSemanticSignature>) relatedB);
        } else {
            return 0;
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
        for (StringSemanticSymbol ss : this) {
            symbols.add(ss.getSymbol());
        }
        return symbols;
    }

    @Override
    public Iterator<StringSemanticSymbol> iterator() {
        return symbols.iterator();
    }

    @Override
    public StringSemanticSignature appendSignature(StringSemanticSignature other) {
        for (StringSemanticSymbol ss : other) {
            addSymbol(ss);
        }
        return this;
    }

    public String toString() {
        String output = "";
        for (StringSemanticSymbol semanticSymbol : symbols) {
            output += String.format(" %s", semanticSymbol.getSymbol());
        }
        return output;
    }


    @Override
    public StringSemanticSignature mergeSignatures(StringSemanticSignature other) {
        return new StringSemanticSignatureImpl(symbols).appendSignature(other);
    }

    @Override
    public StringSemanticSymbol getSymbol(int index) {
        return symbols.get(index);
    }

    @Override
    public void addSymbol(StringSemanticSymbol symbol) {
        symbols.add(symbol);
    }

    @Override
    public StringSemanticSignature copy() {
        return new StringSemanticSignatureImpl(symbols);
    }

    @Override
    public int size() {
        return symbols.size();
    }
}
