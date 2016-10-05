package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.VectorOperation;
import org.getalp.lexsema.util.word2vec.Word2VecClient;
import java.util.Arrays;

public class VectorizationSignatureEnrichment2 extends SignatureEnrichmentAbstract {

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature, String id) {
        SemanticSignature newSignature = new SemanticSignatureImpl();
        double[] vectorSum = null;
        for (SemanticSymbol symbol : signature) {
            double[] vector = Word2VecClient.getWordVector(symbol.getSymbol().toLowerCase());
            if (vector.length == 0) continue;
            if (vectorSum == null) vectorSum = vector;
            else vectorSum = VectorOperation.add(vectorSum, vector);
        }
        if (vectorSum == null)
        {
        	System.err.println("Warning : cannot vectorize sense \"" + id + "\"");
        	return signature;
        }
        else
        {
        	vectorSum = VectorOperation.normalize(vectorSum);
        	newSignature.addSymbol(Arrays.toString(vectorSum).replace(" ", ""));
        	return newSignature;
        }
    }

    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature) {
    	return enrichSemanticSignature(signature, "");
    }
}
