package org.getalp.lexsema.similarity.signatures;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.Language;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface SemanticSignature extends Iterable<SemanticSymbol>, Serializable {

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
     * Add a symbolString of symbols and their associated weights as a collection
     *
     * @param symbolString  The symbol symbolString
     * @param weights The respective weights of the symbols
     */
    void addSymbolString(List<String> symbolString, List<Double> weights);

    /**
     * Add a symbolString of symbols
     *
     * @param symbolString  The symbol symbolString
     */
    void addSymbolString(List<String> symbolString);


    SemanticSignature appendSignature(SemanticSignature other);

    /**
     * Merge the present semantic signature with {@code other} and returns a copy of the
     * annotation result
     *
     * @param other The other semantic signature to merge with the current one
     * @return The copy of the merged signature
     */
    SemanticSignature mergeSignatures(SemanticSignature other);

    /**
     * Add a semantic symbol represented by {@code symbol}
     *
     * @param symbol the semantic symbol
     */
    void addSymbol(SemanticSymbol symbol);

    SemanticSignature copy();

    List<String> getStringSymbols();
    List<SemanticSymbol> getSymbols();
    SemanticSymbol getSymbol(int index);
    boolean isNull();
}
