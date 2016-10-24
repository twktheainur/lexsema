package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.word2vec.Word2VecClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class VectorizationSignatureEnrichment extends SignatureEnrichmentAbstract {

    private static final Logger logger = LoggerFactory.getLogger(VectorizationSignatureEnrichment.class);

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
        SemanticSignature newSignature = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature();
        for (SemanticSymbol symbol : semanticSignature) {
        	String symbolStr = symbol.getSymbol().toLowerCase().trim();
            double[] vector = Word2VecClient.getWordVector(symbolStr);
            if (vector.length == 0) {
                logger.warn("Warning : cannot vectorize word \"{}\"", symbolStr);
            } else {
                newSignature.addSymbol(Arrays.toString(vector).replace(" ", ""));
            }
        }
        return newSignature;
    }

}
