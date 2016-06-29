package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.word2vec.Word2VecClient;
import java.util.Arrays;

public class VectorizationSignatureEnrichment extends SignatureEnrichment {

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature) {
        SemanticSignature newSignature = new SemanticSignatureImpl();
        for (SemanticSymbol symbol : signature) {
        	String symbolStr = symbol.getSymbol().toLowerCase().trim();
            double[] vector = Word2VecClient.getWordVector(symbolStr);
            if (vector.length != 0) newSignature.addSymbol(Arrays.toString(vector).replace(" ", ""));
            else System.err.println("Warning : cannot vectorize word \"" + symbolStr + "\"");
        }
        return newSignature;
    }

}
