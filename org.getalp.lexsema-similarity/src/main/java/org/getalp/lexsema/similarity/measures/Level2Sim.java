package org.getalp.lexsema.similarity.measures;

import com.wcohen.ss.AbstractTokenizedStringDistance;
import org.getalp.lexsema.similarity.signatures.SemanticSignature;

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
    public double compute(SemanticSignature sigA, SemanticSignature sigB,
                          Map<String, SemanticSignature> relatedSignaturesA,
                          Map<String, SemanticSignature> relatedSignaturesB) {
        return compute(sigA.getSymbols(), sigB.getSymbols());
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
