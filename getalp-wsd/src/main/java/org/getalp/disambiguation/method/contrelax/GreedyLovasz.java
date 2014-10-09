package org.getalp.disambiguation.method.contrelax;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.similarity.local.SimilarityMeasure;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.Sense;
import org.getalp.disambiguation.Word;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.Disambiguator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class GreedyLovasz implements Disambiguator {

    private int window;
    private SimilarityMeasure sim;
    private boolean addSenseSignatures;
    private boolean onlyOverlapContexts;
    private boolean onlyUniqueWords;
    private boolean includeTarget;
    private boolean allowTies;
    private boolean fallbackFS;

    public GreedyLovasz(int window, SimilarityMeasure sim, boolean addSenseSignatures, boolean onlyOverlapContexts, boolean onlyUniqueWords, boolean includeTarget, boolean allowTies, boolean fallbackFS) {
        this.window = window;
        this.sim = sim;
        this.fallbackFS = fallbackFS;
        this.addSenseSignatures = addSenseSignatures;
        this.onlyOverlapContexts = onlyOverlapContexts;
        this.onlyUniqueWords = onlyUniqueWords;
        this.includeTarget = includeTarget;
        this.allowTies = allowTies;
    }

    @Override
    public Configuration disambiguate(Document document) {
        Configuration c = new Configuration(document);

        for (int i = 0; i < document.getWords().size(); i++) {
            System.err.print(String.format("\tDisambiguating: %.2f%%\r",((double)i/(double)document.getWords().size())*100d));
            int start = (i <= window) ? 0 : i - window;
            int end = (i + window < document.getWords().size()) ? i + window : document.getWords().size();
            Collection<String> context;
            if (onlyUniqueWords) {
                context = new TreeSet<String>();
            } else {
                context = new ArrayList<String>();
            }

            ScaledLevenstein sl = new ScaledLevenstein();
            for (int j = start; j < end; j++) {
                if (includeTarget || (!includeTarget && j != i)) {
                    Word cw = document.getWords().get(j);
                    boolean isOverlap = false;
                    if (onlyOverlapContexts) {
                        for (Sense s : document.getSense().get(i)) {
                            for (String wd : s.getSignature()) {
                                if (sl.score(cw.getSurfaceForm(), wd) > 0) {
                                    isOverlap = true;
                                    break;
                                }
                            }
                        }
                    }
                    if ((onlyOverlapContexts && isOverlap) || (!onlyOverlapContexts)) {
                        context.add(cw.getSurfaceForm());
                    }
                    if (addSenseSignatures) {
                        for (Sense s : document.getSense().get(j)) {
                            context.addAll(s.getSignature());

                        }
                    }
                }
            }
            List<String> lcontext = new ArrayList<String>();
            lcontext.addAll(context);
            int maxIndex = -1;
            double maxValue = -Double.MAX_VALUE;
            double prevMaxValue = 0;
            for (int s = 0; s < document.getSense().get(i).size(); s++) {

                double score = sim.compute(document.getSense().get(i).get(s), lcontext);
                //System.err.println(score);
                if (score > maxValue) {
                    prevMaxValue = maxValue;
                    maxValue = score;
                    maxIndex = s;
                }
            }
            if (!allowTies && Math.abs(prevMaxValue - maxValue) < 0.00001) {
                maxIndex = -1;
            }
            if(fallbackFS && maxIndex ==-1) maxIndex = 0;
            c.setSense(i, maxIndex);
            c.setConfidence(i, 1d);
        }
        return c;
    }
}
