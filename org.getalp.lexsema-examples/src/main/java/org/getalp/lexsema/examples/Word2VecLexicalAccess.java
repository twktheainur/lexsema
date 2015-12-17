package org.getalp.lexsema.examples;

import org.getalp.lexsema.io.word2vec.MultilingualSerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.MultilingualWord2VecLoader;
import org.getalp.lexsema.io.word2vec.SerializedModelWord2VecLoader;
import org.getalp.lexsema.io.word2vec.Word2VecLoader;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.word2vec.Word2VecGlossCosineSimilarity;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public final class Word2VecLexicalAccess {
    private static final Logger logger = LoggerFactory.getLogger(Word2VecLexicalAccess.class);
    private static final Pattern PUNCTPATTERN = Pattern.compile("\\p{Punct}");

    private Word2VecLexicalAccess() {
    }


    public static void main(String... args) throws IOException {

        if (args.length <2) {
            usage();
        }
        SemanticSignature signature1 = new SemanticSignatureImpl(PUNCTPATTERN.matcher(args[0]).replaceAll("").toLowerCase().trim());
        SemanticSignature signature2 = new SemanticSignatureImpl(PUNCTPATTERN.matcher(args[1]).replaceAll("").toLowerCase().trim());

        Word2VecLoader word2VecLoader = new SerializedModelWord2VecLoader();
        word2VecLoader.loadGoogle(new File(args[2]),true);

        SimilarityMeasure similarityMeasure = new Word2VecGlossCosineSimilarity(word2VecLoader.getWord2Vec(),true);
        double sim = similarityMeasure.compute(signature1, signature2);
        String output = String.format("The similarity between \"%s\" and \"%s\" is %s", signature1.toString(), signature2.toString(), sim);
        logger.info(output);
    }

    private static void usage() {
        logger.error("Usage -- org.getalp.lexsema.examples.TextSimilarity \"String1\" \"String2\"");
        System.exit(1);
    }
}
