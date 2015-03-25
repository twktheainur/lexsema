package org.getalp.lexsema.acceptali.word2vec;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.getalp.lexsema.similarity.signatures.*;

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

    private List<SemanticSymbol> enrichSemanticSymbol(SemanticSymbol semanticSymbol) {
        String word = semanticSymbol.getSymbol();
        word = word.replaceAll("\\p{Punct}", "");
        Collection<String> related = word2Vec.wordsNearest(word, topN);
        List<SemanticSymbol> symbols = new ArrayList<>();
        for (String sword : related) {
            symbols.add(new SemanticSymbolImpl(sword, 1.0));
        }
        return symbols;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (SemanticSymbol semanticSymbol : semanticSignature) {
            newSignature.addSymbols(enrichSemanticSymbol(semanticSymbol));
        }
        return newSignature;
    }

    @Override
    public void close() {

    }
}
