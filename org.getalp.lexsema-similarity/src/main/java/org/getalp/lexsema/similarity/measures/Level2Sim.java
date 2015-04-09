package org.getalp.lexsema.similarity.measures;

import com.wcohen.ss.AbstractTokenizedStringDistance;
import org.getalp.lexsema.similarity.signatures.IndexedSemanticSignature;
import org.getalp.lexsema.similarity.signatures.StringSemanticSignature;

import java.util.Map;


public class Level2Sim implements SimilarityMeasure {

    AbstractTokenizedStringDistance distance;

    public Level2Sim(AbstractTokenizedStringDistance distance) {
        this.distance = distance;
    }


    private double compute(String a, String b) {
        return distance.score(a, b);
    }

    @Override
    public double compute(StringSemanticSignature sigA, StringSemanticSignature sigB,
                          Map<String, StringSemanticSignature> relatedSignaturesA,
                          Map<String, StringSemanticSignature> relatedSignaturesB) {
        return compute(sigA.getSymbols(), sigB.getSymbols());
    }

    @Override
    public double compute(IndexedSemanticSignature sigA, IndexedSemanticSignature sigB,
                          Map<String, IndexedSemanticSignature> relatedSignaturesA,
                          Map<String, IndexedSemanticSignature> relatedSignaturesB) {
        return 0;
    }

    private double compute(Iterable<String> a, Iterable<String> b) {
        String sa = "";
        String sb = "";
        for (String s : a) {
            sa += a + " ";
        }
        sa = sa.trim();
        for (String s : b) {
            sb += b + " ";
        }
        sb = sb.trim();

        return compute(sa, sb);
    }
}
