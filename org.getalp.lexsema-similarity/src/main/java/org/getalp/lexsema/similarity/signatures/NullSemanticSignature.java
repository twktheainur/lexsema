package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbolImpl;
import org.getalp.lexsema.util.Language;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public final class NullSemanticSignature implements SemanticSignature{
    private static final SemanticSignature instance = new NullSemanticSignature();

    public static SemanticSignature getInstance() {
        return instance;
    }

    private NullSemanticSignature() {
    }

    @Override
    public double computeSimilarityWith(SimilarityMeasure measure, SemanticSignature other, Map<String, SemanticSignature> relatedA, Map<String, SemanticSignature> relatedB) {
        return 0;
    }

    @Override
    public List<Double> getWeights() {
        return Collections.emptyList();
    }

    @Override
    public Language getLanguage() {
        return Language.UNSUPPORTED;
    }

    @Override
    public void setLanguage(Language language) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void addSymbol(String symbol, double weight) {

    }

    @Override
    public void addSymbol(String symbol) {

    }

    @Override
    public void addSymbols(List<SemanticSymbol> symbols) {

    }

    @Override
    public void addSymbolString(List<String> string, List<Double> weights) {

    }

    @Override
    public void addSymbolString(List<String> string) {

    }

    @Override
    public SemanticSignature appendSignature(SemanticSignature other) {
        return instance;
    }

    @Override
    public SemanticSignature mergeSignatures(SemanticSignature other) {
        return instance;
    }

    @Override
    public void addSymbol(SemanticSymbol symbol) {

    }

    @Override
    public SemanticSignature copy() {
        return instance;
    }

    @Override
    public List<String> getStringSymbols() {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticSymbol> getSymbols() {
        return Collections.emptyList();
    }

    @Override
    public SemanticSymbol getSymbol(int index) {
        return new SemanticSymbolImpl("",0);
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public Iterator<SemanticSymbol> iterator() {
        final List<SemanticSymbol> emptyList = Collections.<SemanticSymbol>emptyList();
        return emptyList.iterator();
    }
}
