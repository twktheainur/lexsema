package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.util.Language;

import java.util.*;
import java.util.regex.Pattern;


public class SemanticSignatureImpl implements SemanticSignature {

    public static final double DEFAULT_WEIGHT = 1d;
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private final List<SemanticSymbol> symbols;
    private Language language = Language.UNSUPPORTED;

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }

    public SemanticSignatureImpl() {
        symbols = new ArrayList<>();
    }

    public SemanticSignatureImpl(CharSequence symbolString) {
        symbols = new ArrayList<>();
        String[] tokens = WHITESPACE.split(symbolString);
        for(String token: tokens){
            addSymbol(token);
        }
    }

    private SemanticSignatureImpl(List<SemanticSymbol> symbols) {
        this.symbols = new ArrayList<>();
        Collections.copy(symbols, this.symbols);
    }

    @Override
    public double computeSimilarityWith(SimilarityMeasure measure, SemanticSignature other,
                                        Map<String, SemanticSignature> relatedA,
                                        Map<String, SemanticSignature> relatedB) {
        if (other != null) {
            return measure.compute(this, other,
                    relatedA,
                    relatedB);
        } else {
            return 0;
        }
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
        List<String> srtingSymbols = new ArrayList<>();
        for (SemanticSymbol ss : this) {
            srtingSymbols.add(ss.getSymbol());
        }
        return srtingSymbols;
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
        StringBuilder stringBuilder = new StringBuilder();
        for (SemanticSymbol semanticSymbol : symbols) {
            stringBuilder.append(String.format(" %s", semanticSymbol.getSymbol()));
        }
        return stringBuilder.toString();
    }


    @Override
    public SemanticSignature mergeSignatures(SemanticSignature other) {
        final SemanticSignature semanticSymbols = new SemanticSignatureImpl(symbols);
        return semanticSymbols.appendSignature(other);
    }

    @Override
    public SemanticSymbol getSymbol(int index) {
        return symbols.get(index);
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
