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

public class VectorizationSignatureEnrichment extends SignatureEnrichment {

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature) {
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (SemanticSymbol symbol : signature) {
            double[] vector = Word2VecClient.getWordVector(symbol.getSymbol());
            if (vector.length != 0) newSignature.addSymbol(Arrays.toString(vector).replace(" ", ""));
            else System.err.println("Warning : cannot vectorize " + symbol.getSymbol());
        }
        return newSignature;
    }

}
