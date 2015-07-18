package org.getalp.lexsema.examples;

import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;
import org.getalp.lexsema.similarity.signatures.SemanticSignatureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TextSimilarity {
    private static Logger logger = LoggerFactory.getLogger(TextSimilarity.class);

    public static void main(String[] args) {

        if (args.length <2) {
            usage();
        }
        SemanticSignature signature1 = new SemanticSignatureImpl(args[0]);
        SemanticSignature signature2 = new SemanticSignatureImpl(args[1]);

        SimilarityMeasure similarityMeasure = new TverskiIndexSimilarityMeasureBuilder()
                .alpha(1d).beta(0).gamma(0).computeRatio(false).fuzzyMatching(false).normalize(true).regularizeOverlapInput(true).build();
        double sim = similarityMeasure.compute(signature1, signature2);
        String output = String.format("The similarity between \"%s\" and \"%s\" is %s", signature1.toString(), signature2.toString(), sim);
        logger.info(output);
    }

    private static void usage() {
        logger.error("Usage -- org.getalp.lexsema.examples.TextSimilarity \"String1\" \"String2\"");
        System.exit(1);
    }
}
