package org.getalp.lexsema.similarity.signatures;


import org.getalp.lexsema.util.segmentation.Segmenter;

import java.util.List;

/**
 * Represent the semantic signature of a semantic unit of meaning (e.g. LexicalSense)
 */
public interface SemanticSignature extends Iterable<SemanticSymbol> {
    /**
     * Adds a weighted symbol to the semantic signature
     *
     * @param symbol The string representative of the symbol
     * @param weight The weight associated to the symbol
     */
    public void addSymbol(String symbol, double weight);

    /**
     * Adds a symbol to the signature
     *
     * @param symbol The string representative of the symbol
     */
    public void addSymbol(String symbol);


    /**
     * Add a string of symbols with the default weight. The symbols will be split by spaces
     *
     * @param string The symbol string
     */
    public void addSymbolString(String string);

    /**
     * Add a string of symbols with the default weight and
     * split into tokens with the provided Segmenter.
     *
     * @param string The symbol string
     */
    public void addSymbolString(String string, Segmenter segmenter);

    /**
     * Add a string of symbols and their associated weights as a collection
     *
     * @param string  The symbol string
     * @param weights The respective weights of the symbols
     */
    public void addSymbolString(List<String> string, List<Double> weights);

    /**
     * Add a string of symbols with the default weight (1.0)
     *
     * @param string The symbol string
     */
    public void addSymbolString(List<String> string);

    /**
     * Add a semantic symbol represented by <code>symbol</code>
     *
     * @param symbol the semantic symbol
     */
    public void addSymbol(SemanticSymbol symbol);

    /**
     * Add a collection of semantic symbols
     *
     * @param symbols The collection of semantic symbols to add
     */
    public void addSymbols(List<SemanticSymbol> symbols);


    /**
     * Appends the semantic signature <code>others</code> with this <code>SemanticSignature</code>
     *
     * @param other The other SemanticSignature to append
     */
    public SemanticSignature appendSignature(SemanticSignature other);

    /**
     * Merge the present semantic signature with <code>other</code> and returns a copy of the
     * annotresult
     *
     * @param other The other semantic signature to merge with the current one
     * @return The copy of the merged signature
     */
    public SemanticSignature mergeSignatures(SemanticSignature other);

    public List<String> getSymbols();

    public List<Double> getWeights();

    public SemanticSignature copy();
}
