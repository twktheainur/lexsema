package org.getalp.lexsema.examples;

import org.getalp.lexsema.io.word2vec.MultilingualSerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.MultilingualWord2VecLoader;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.word2vec.Word2VecGlossCosineSimilarity;
import org.getalp.lexsema.similarity.signatures.DefaultSemanticSignatureFactory;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class TextSimilarityEmbeddings {
    private static final Logger logger = LoggerFactory.getLogger(TextSimilarityEmbeddings.class);

    private TextSimilarityEmbeddings() {
    }

    public static void main(String... args) throws IOException {

        if (args.length <2) {
            usage();
        }
        SemanticSignature signature1 = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature(args[0]);
        SemanticSignature signature2 = DefaultSemanticSignatureFactory.DEFAULT.createSemanticSignature(args[1]);

        MultilingualWord2VecLoader word2VecLoader = new MultilingualSerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(new File(args[2]),true);

        //SimilarityMeasure similarityMeasure = new Word2VecGlossDistanceSimilarity(word2VecLoader.getWordVectors(Language.ENGLISH),new MahalanobisDistance(),null);
        SimilarityMeasure similarityMeasure = new Word2VecGlossCosineSimilarity(word2VecLoader.getWordVectors(Language.ENGLISH),true);
        double sim = similarityMeasure.compute(signature1, signature2);
        String output = String.format("The similarity between \"%s\" and \"%s\" is %s", signature1.toString(), signature2.toString(), sim);
        logger.info(output);
    }

    private static void usage() {
        logger.error("Usage -- org.getalp.lexsema.examples.TextSimilarity \"String1\" \"String2\"");
        System.exit(1);
    }
}
