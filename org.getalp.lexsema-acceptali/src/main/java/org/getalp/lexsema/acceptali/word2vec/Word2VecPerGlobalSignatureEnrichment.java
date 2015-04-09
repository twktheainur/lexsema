package org.getalp.lexsema.acceptali.word2vec;

import org.deeplearning4j.berkeley.Counter;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.getalp.lexsema.similarity.signatures.SignatureEnrichment;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

public class Word2VecPerGlobalSignatureEnrichment implements SignatureEnrichment {

    public static final int DEFAULT_TOP_N = 10;

    private Word2Vec word2Vec;
    private VocabCache vocab;
    private int topN;


    public Word2VecPerGlobalSignatureEnrichment(Word2Vec word2Vec, VocabCache vocab) {
        this(word2Vec, vocab, DEFAULT_TOP_N);
    }

    public Word2VecPerGlobalSignatureEnrichment(Word2Vec word2Vec, VocabCache vocab, int topN) {
        this.word2Vec = word2Vec;
        this.vocab = vocab;
        this.topN = topN;
    }


    private VocabCache vocab() {
        return vocab;
    }

    @Override
    public StringSemanticSignature enrichSemanticSignature(StringSemanticSignature semanticSignature) {
        StringSemanticSignature newSignature = new StringSemanticSignatureImpl();
        INDArray definitionVector = word2Vec.getVectorizer().transform(semanticSignature.toString());
        Counter<String> distances = new Counter<>();
        for (String s : vocab().words()) {
            INDArray otherVec = word2Vec.getWordVectorMatrix(s);
            double sim = Transforms.cosineSim(definitionVector, otherVec);
            distances.incrementCount(s, sim);
        }
        distances.keepTopNKeys(topN);
        for (String s : distances.keySet()) {
            newSignature.addSymbol(s, distances.getCount(s));
        }
        return newSignature;
    }

    @Override
    public void close() {

    }
}
