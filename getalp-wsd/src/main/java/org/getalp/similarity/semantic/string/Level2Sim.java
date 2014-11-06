package org.getalp.similarity.semantic.string;

import com.wcohen.ss.AbstractTokenizedStringDistance;
import org.getalp.io.Sense;
import org.getalp.similarity.semantic.SimilarityMeasure;

import java.util.List;


public class Level2Sim implements SimilarityMeasure {

    AbstractTokenizedStringDistance distance;

    public Level2Sim(AbstractTokenizedStringDistance distance) {
        this.distance = distance;
    }

    private double compute(String a, String b) {

        //Level2Levenstein me = new Level2Levenstein();
        //com.wcohen.ss.Level2Sim me = new com.wcohen.ss.Level2Sim();
        return distance.score(a, b);
    }


    public double compute(List<String> a, List<String> b) {
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

    @Override
    public double compute(Sense a, List<String> b) {
        return compute(a.getSignature(), b);
    }

    @Override
    public double compute(List<String> a, Sense b) {
        return compute(a, b.getSignature());
    }

    @Override
    public double compute(Sense a, Sense b) {
        return compute(a.getSignature(), b.getSignature());
    }
}
