package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndex;
import org.getalp.lexsema.similarity.signatures.index.SymbolIndexImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.Language;
import org.getalp.lexsema.util.word2vec.Word2VecClient;

import cern.colt.Arrays;

public class VectorizationSignatureEnrichment implements SignatureEnrichment {

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature) {
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (SemanticSymbol symbol : signature) {
            newSignature.addSymbol(Arrays.toString(Word2VecClient.getWordVector(symbol.getSymbol())).replace(" ", ""));
        }
        return newSignature;
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature, Language language) {
        return enrichSemanticSignature(signature);
    }

    @Override
    public void close() {

    }
    
}
