package org.getalp.lexsema.similarity.signatures.enrichment;

import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.getalp.lexsema.similarity.signatures.symbols.SemanticSymbol;
import org.getalp.lexsema.util.VectorOperation;
import org.getalp.lexsema.util.word2vec.Word2VecClient;
import java.util.Arrays;
import java.util.regex.Pattern;

public class VectorizationSignatureEnrichment3 extends SignatureEnrichment {
	
	double threshold = 0;
	
	Pattern non_letters_pattern = Pattern.compile("[^\\p{IsAlphabetic}]");
	
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
        id = id.substring(0, id.indexOf('%'));
        id = non_letters_pattern.matcher(id).replaceAll("").toLowerCase();
        double[] vectorId = Word2VecClient.getWordVector(id);
        if (vectorId.length == 0)
        {
        	System.err.println("Warning : cannot vectorize " + id);
        	return newSignature;
        }
        for (SemanticSymbol symbol : signature) {
            double[] vector = Word2VecClient.getWordVector(symbol.getSymbol().toLowerCase());
            if (vector.length == 0) continue;
            if (VectorOperation.dot_product(vector, vectorId) < threshold) continue;
            if (vectorSum == null) vectorSum = vector;
            else vectorSum = VectorOperation.add(vectorSum, vector);
        }
        if (vectorSum == null) return newSignature;
        vectorSum = VectorOperation.normalize(vectorSum);
        newSignature.addSymbol(Arrays.toString(vectorSum).replace(" ", ""));
        return newSignature;
    }

	@Override
	public SemanticSignature enrichSemanticSignature(SemanticSignature semanticSignature) {
		return semanticSignature;
	}

}
