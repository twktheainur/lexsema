package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.symbols.IndexedSemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.IndexedSemanticSymbolImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbolImpl;
import org.getalp.lexsema.util.Language;

import java.util.*;
import java.util.stream.Collectors;


public class IndexedSemanticSignatureImpl implements IndexedSemanticSignature {

    public static final double DEFAULT_WEIGHT = 1d;
    private final List<IndexedSemanticSymbol> symbols;

    private Language language = null;
    private final SymbolIndex symbolIndex;

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }

    public IndexedSemanticSignatureImpl(SymbolIndex symbolIndex) {

        symbols = new ArrayList<>();
        this.symbolIndex = symbolIndex;
    }

    private IndexedSemanticSignatureImpl(List<IndexedSemanticSymbol> symbols, SymbolIndex symbolIndex) {
        this.symbols = new ArrayList<>();
        Collections.copy(symbols, this.symbols);
        this.symbolIndex = symbolIndex;
    }


    @Override
    public List<Integer> getIndexedSymbols() {
        List<Integer> intetegerSymbols = new ArrayList<>();
        for (IndexedSemanticSymbol ss : symbols) {
            intetegerSymbols.add(ss.getIndexedSymbol());
        }
        return intetegerSymbols;
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
    public IndexedSemanticSymbol getIndexedSymbol(int index) {
        return symbols.get(index);
    }



    @Override
    public IndexedSemanticSignature copy() {
        return new IndexedSemanticSignatureImpl(symbols,symbolIndex);
    }

    @Override
    public int size() {
        return symbols.size();
    }
    
    @Override
    public void sort() {
        Collections.sort(symbols);
    }

    @Override
    public void addSymbol(String symbol, double weight) {
        symbols.add(new IndexedSemanticSymbolImpl(symbolIndex.getSymbolIndex(symbol), weight));
    }

    @Override
    public void addIndexedSymbol(Integer symbol) {
        symbols.add(new IndexedSemanticSymbolImpl(symbol, 0));
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
    public List<String> getStringSymbols() {
        List<String> stringSymbols = new ArrayList<>();
        for (SemanticSymbol ss : this) {
            stringSymbols.add(ss.getSymbol());
        }
        return stringSymbols;
    }

    @Override
    public List<SemanticSymbol> getSymbols() {
        return symbols.stream().map((IndexedSemanticSymbol symbol) -> (SemanticSymbol)symbol).collect(Collectors.toList());
    }

    @Override
    public Iterator<SemanticSymbol> iterator() {
        final Collection<SemanticSymbol> stringSymbols = new ArrayList<>();
        for(IndexedSemanticSymbol semanticSymbol: symbols){
            stringSymbols.add(new SemanticSymbolImpl(semanticSymbol.getSymbol(), semanticSymbol.getWeight()));
        }
        return stringSymbols.iterator();
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
        final SemanticSignature semanticSymbols = new IndexedSemanticSignatureImpl(symbols, symbolIndex);
        return semanticSymbols.appendSignature(other);
    }

    @Override
    public SemanticSymbol getSymbol(int index) {
        return symbols.get(index);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public void addSymbol(SemanticSymbol symbol) {
        if(symbol instanceof IndexedSemanticSymbol){
            symbols.add((IndexedSemanticSymbol) symbol);
        } else {
            Integer iSymbol = symbolIndex.getSymbolIndex(symbol.getSymbol());
            IndexedSemanticSymbol indexedSemanticSymbol = new IndexedSemanticSymbolImpl(iSymbol,symbol.getWeight());
            symbols.add(indexedSemanticSymbol);
        }
    }


}
