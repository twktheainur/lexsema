package org.getalp.lexsema.similarity.signatures.enrichment;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.StringSemanticSymbol;
import org.getalp.lexsema.similarity.signatures.StringSemanticSymbolImpl;
import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Word2VecLocalSignatureEnrichment implements SignatureEnrichment {

    public static final int DEFAULT_TOP_N = 10;

    private Word2Vec word2Vec;
    private int topN;


    public Word2VecLocalSignatureEnrichment(Word2Vec word2Vec) {
        this(word2Vec, DEFAULT_TOP_N);
    }

    public Word2VecLocalSignatureEnrichment(Word2Vec word2Vec, int topN) {
        this.word2Vec = word2Vec;
        this.topN = topN;
    }

    private List<StringSemanticSymbol> enrichSemanticSymbol(StringSemanticSymbol semanticSymbol) {
        String word = semanticSymbol.getSymbol();
        word = word.replaceAll("\\p{Punct}", "");
        Collection<String> related = word2Vec.wordsNearest(word, topN);
        List<StringSemanticSymbol> symbols = new ArrayList<>();
        for (String sword : related) {
            symbols.add(new StringSemanticSymbolImpl(sword, 1.0));
        }
        return symbols;
    }

    @Override
    public StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature) {
        StringSemanticSignature newSignature = new StringSemanticSignatureImpl();
        for (StringSemanticSymbol semanticSymbol : semanticSignature) {
            newSignature.addSymbols(enrichSemanticSymbol(semanticSymbol));
        }
        return newSignature;
    }

    @Override
    public StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature, Language language) {
        return null;
    }

    @Override
    public void close() {

    }
}
