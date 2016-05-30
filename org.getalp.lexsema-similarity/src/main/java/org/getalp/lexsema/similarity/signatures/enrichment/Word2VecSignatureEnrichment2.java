package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.util.word2vec.Word2VecClient;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbolImpl;
import org.getalp.lexsema.util.Language;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Word2VecSignatureEnrichment2 extends SignatureEnrichment {

    private final int topN;
    
    public Word2VecSignatureEnrichment2(int topN) {
        this.topN = topN;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        List<List<SemanticSymbol>> newSymbols = new ArrayList<>();
        for (SemanticSymbol symbol : semanticSignature) {
            newSymbols.add(enrichSemanticSymbol(symbol));
        }
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (String word : semanticSignature.getStringSymbols()) {
            newSignature.addSymbol(word);
        }
        for (List<SemanticSymbol> semanticSymbols : newSymbols) {
            for (SemanticSymbol semanticSymbol : semanticSymbols) {
                newSignature.addSymbol(semanticSymbol);
            }
        }
        return newSignature;
    }
    
    private List<SemanticSymbol> enrichSemanticSymbol(SemanticSymbol semanticSymbol) {
        List<SemanticSymbol> ret = new ArrayList<>();
        Collection<String> words = Word2VecClient.getMostSimilarWords(semanticSymbol.getSymbol(), topN);
        for (String word : words) {
            ret.add(new SemanticSymbolImpl(word, 1));
        }
        return ret;
    }
}
