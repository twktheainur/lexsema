package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.Language;

import java.util.List;
import java.util.Map;


public interface SemanticSignature extends Iterable<SemanticSymbol> {

    double computeSimilarityWith(SimilarityMeasure measure, SemanticSignature other,
                                 Map<String,SemanticSignature> relatedA,
                                 Map<String,SemanticSignature> relatedB
    );
    List<Double> getWeights();
    Language getLanguage();
    void setLanguage(Language language);
    int size();

    /**
     * Adds a weighted symbol to the semantic signature
     *
     * @param symbol The string representative of the symbol
     * @param weight The weight associated to the symbol
     */
    void addSymbol(String symbol, double weight);

    /**
     * Adds a symbol to the signature
     *
     * @param symbol The string representative of the symbol
     */
    void addSymbol(String symbol);


    /**
     * Add a string of symbols
     *
     * @param symbols The symbol string
     */
    void addSymbols(List<SemanticSymbol> symbols);

    /**
     * Add a string of symbols and their associated weights as a collection
     *
     * @param string  The symbol string
     * @param weights The respective weights of the symbols
     */
    void addSymbolString(List<String> string, List<Double> weights);

    void addSymbolString(List<String> string);

    SemanticSignature appendSignature(SemanticSignature other);

    /**
     * Merge the present semantic signature with <code>other</code> and returns a copy of the
     * annotresult
     *
     * @param other The other semantic signature to merge with the current one
     * @return The copy of the merged signature
     */
    SemanticSignature mergeSignatures(SemanticSignature other);

    /**
     * Add a semantic symbol represented by <code>symbol</code>
     *
     * @param symbol the semantic symbol
     */
    void addSymbol(SemanticSymbol symbol);

    SemanticSignature copy();

    List<String> getSymbols();

    public SemanticSymbol getSymbol(int index);
}
