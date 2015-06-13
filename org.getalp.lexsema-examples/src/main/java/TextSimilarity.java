import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.similarity.measures.tverski.TverskiIndexSimilarityMeasureBuilder;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignatureImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TextSimilarity {
    private static Logger logger = LoggerFactory.getLogger(TextSimilarity.class);

    public static void main(String[] args) {

        if (args.length <2) {
            usage();
        }
        StringSemanticSignature signature1 = new StringSemanticSignatureImpl(args[0]);
        StringSemanticSignature signature2 = new StringSemanticSignatureImpl(args[1]);

        SimilarityMeasure similarityMeasure = new TverskiIndexSimilarityMeasureBuilder()
                .alpha(1d).beta(0).gamma(0).computeRatio(false).fuzzyMatching(false).normalize(true).regularizeOverlapInput(true).build();
        double sim = similarityMeasure.compute(signature1, signature2);
        String output = String.format("The similarity between \"%s\" and \"%s\" is %s", signature1.toString(), signature2.toString(), sim);
        logger.info(output);
    }

    private static void usage() {
        logger.error("Usage -- TextSimilarity \"String1\" \"String2\"");
        System.exit(1);
    }
}
