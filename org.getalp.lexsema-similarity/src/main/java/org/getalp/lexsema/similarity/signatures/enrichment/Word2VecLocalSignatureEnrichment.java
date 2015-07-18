package org.getalp.lexsema.similarity.signatures.enrichment;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.SemanticSymbolImpl;
import org.getalp.lexsema.util.Language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Word2VecLocalSignatureEnrichment implements SignatureEnrichment {

    public static final int DEFAULT_TOP_N = 10;
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{Punct}");

    private final Word2Vec word2Vec;
    private final int topN;


    public Word2VecLocalSignatureEnrichment(Word2Vec word2Vec) {
        this(word2Vec, DEFAULT_TOP_N);
    }

    public Word2VecLocalSignatureEnrichment(Word2Vec word2Vec, int topN) {
        this.word2Vec = word2Vec;
        this.topN = topN;
    }

    private List<SemanticSymbol> enrichSemanticSymbol(SemanticSymbol semanticSymbol) {
        String word = semanticSymbol.getSymbol();
        final Matcher matcher = PUNCTUATION_PATTERN.matcher(word);
        Collection<String> related = word2Vec.wordsNearest(matcher.replaceAll(""), topN);
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
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature, Language language) {
        return null;
    }

    @Override
    public void close() {

    }
}
