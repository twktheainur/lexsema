package org.getalp.lexsema.examples;

import org.getalp.lexsema.io.word2vec.MultilingualSerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.MultilingualWord2VecLoader;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.similarity.measures.word2vec.Word2VecGlossSimilarity;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.getalp.lexsema.util.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class Word2VecTextSimilarity {
    private static Logger logger = LoggerFactory.getLogger(Word2VecTextSimilarity.class);

    public static void main(String[] args) throws IOException {

        if (args.length <2) {
            usage();
        }
        StringSemanticSignature signature1 = new StringSemanticSignatureImpl(args[0].replaceAll("\\p{Punct}","").toLowerCase().trim());
        StringSemanticSignature signature2 = new StringSemanticSignatureImpl(args[1].replaceAll("\\p{Punct}","").toLowerCase().trim());

        MultilingualWord2VecLoader word2VecLoader = new MultilingualSerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(new File(args[2]),true);

        SimilarityMeasure similarityMeasure = new Word2VecGlossSimilarity(word2VecLoader.getWord2Vec(Language.ENGLISH));
        double sim = similarityMeasure.compute(signature1, signature2);
        String output = String.format("The similarity between \"%s\" and \"%s\" is %s", signature1.toString(), signature2.toString(), sim);
        logger.info(output);
    }

    private static void usage() {
        logger.error("Usage -- org.getalp.lexsema.examples.TextSimilarity \"String1\" \"String2\"");
        System.exit(1);
    }
}
