package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.VectorOperation;
import org.getalp.lexsema.util.word2vec.Word2VecClient;
import java.util.Arrays;

public class VectorizationSignatureEnrichment3 extends SignatureEnrichment {
	
	double threshold = 0;
	
	public VectorizationSignatureEnrichment3() {
		this(0);
	}

	public VectorizationSignatureEnrichment3(double threshold) {
		this.threshold = threshold;
	}
	
    @Override
    public SemanticSignature enrichSemanticSignature(SemanticSignature signature, String id) {
        SemanticSignature newSignature = new SemanticSignatureImpl();
        double[] vectorSum = null;
        double[] vectorId = Word2VecClient.getWordVector(id);
        for (SemanticSymbol symbol : signature) {
            double[] vector = Word2VecClient.getWordVector(symbol.getSymbol().toLowerCase());
            if (vector.length == 0) continue;
            if (VectorOperation.dot_product(vector, vectorId) < threshold) continue;
            if (vectorSum == null) vectorSum = vector;
            else vectorSum = VectorOperation.add(vectorSum, vector);
        }
        vectorSum = VectorOperation.normalize(vectorSum);
        newSignature.addSymbol(Arrays.toString(vectorSum).replace(" ", ""));
        return newSignature;
    }

	@Override
	public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
		return semanticSignature;
	}

}
