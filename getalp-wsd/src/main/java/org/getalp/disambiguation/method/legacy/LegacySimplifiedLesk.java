package org.getalp.disambiguation.method.legacy;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.LexicalEntry;
import org.getalp.disambiguation.Sense;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.Disambiguator;
import org.getalp.similarity.local.SimilarityMeasure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class LegacySimplifiedLesk implements Disambiguator {

    private int window;
    private SimilarityMeasure sim;
    private boolean addSenseSignatures;
    private boolean onlyOverlapContexts;
    private boolean onlyUniqueWords;
    private boolean includeTarget;
    private boolean allowTies;
    private boolean fallbackFS;
    private boolean minimize;
    private double deltaThreshold;

    public LegacySimplifiedLesk(int window, SimilarityMeasure sim, boolean addSenseSignatures, boolean onlyOverlapContexts, boolean onlyUniqueWords, boolean includeTarget, boolean allowTies, boolean fallbackFS, boolean minimize) {
        this.window = window;
        this.sim = sim;
        this.fallbackFS = fallbackFS;
        this.addSenseSignatures = addSenseSignatures;
        this.onlyOverlapContexts = onlyOverlapContexts;
        this.onlyUniqueWords = onlyUniqueWords;
        this.includeTarget = includeTarget;
        this.allowTies = allowTies;
        this.minimize = minimize;
        deltaThreshold = 0.005d;
    }

    @Override
    public Configuration disambiguate(Document document) {
        return disambiguate(document, null);
    }

    @Override
    public Configuration disambiguate(Document document, Configuration c) {
        boolean progressChecked = false;
        if (c == null) {
            c = new Configuration(document);
        }

        for (int i = 0; i < document.getLexicalEntries().size(); i++) {
            System.err.print(String.format("\tDisambiguating: %.2f%%\r", ((double) i / (double) document.getLexicalEntries().size()) * 100d));
            int start = (i <= window) ? 0 : i - window;
            int end = (i + window < document.getLexicalEntries().size()) ? i + window : document.getLexicalEntries().size();
            Collection<String> context;
            if (onlyUniqueWords) {
                context = new TreeSet<String>();
            } else {
                context = new ArrayList<String>();
            }

            ScaledLevenstein sl = new ScaledLevenstein();
            for (int j = start; j < end; j++) {
                if (includeTarget || (!includeTarget && j != i)) {
                    LexicalEntry cw = document.getLexicalEntries().get(j);
                    boolean isOverlap = false;
                    if (onlyOverlapContexts) {
                        for (Sense s : document.getSenses().get(i)) {
                            for (String wd : s.getSignature()) {
                                if (sl.score(cw.getSurfaceForm(), wd) > 0) {
                                    isOverlap = true;
                                    break;
                                }
                            }
                        }
                    }
                    if ((onlyOverlapContexts && isOverlap) || (!onlyOverlapContexts)) {
                        for (String precWord : cw.getPrecedingNonInstances()) {
                            context.add(precWord);
                        }
                        context.add(cw.getSurfaceForm());
                    }
                    if (addSenseSignatures) {
                        for (Sense s : document.getSenses().get(j)) {
                            context.addAll(s.getSignature());

                        }
                    }
                }
            }
            List<String> lcontext = new ArrayList<String>();
            lcontext.addAll(context);
            int index = -1;
            int minIndex = -1;
            int maxIndex = -1;
            double minValue;
            double prevMinValue;
            double maxValue;
            double prevMaxValue;

            minValue = Double.MAX_VALUE;
            prevMinValue = 0;

            maxValue = -Double.MAX_VALUE;
            prevMaxValue = 0;

            for (int s = 0; s < document.getSenses().get(i).size(); s++) {
                double score = sim.compute(document.getSenses().get(i).get(s), lcontext);
                //System.err.println(score);
                if (score <= minValue) {
                    prevMinValue = minValue;
                    minValue = score;
                    minIndex = s;
                } else if (score >= maxValue) {
                    prevMaxValue = maxValue;
                    maxValue = score;
                    maxIndex = s;
                }
            }
            double range = maxValue - minValue;
            double delta = range * deltaThreshold;
            if (!allowTies && ((minimize && Math.abs(prevMinValue - minValue) < deltaThreshold) ||
                    (!minimize && Math.abs(prevMaxValue - maxValue) < deltaThreshold))) {
                index = -1;
            } else if (minimize) {
                index = minIndex;
            } else {
                index = maxIndex;
            }

            if (fallbackFS && index == -1) index = 0;
            c.setSense(i, index);
            c.setConfidence(i, 1d);
        }
        return c;
    }

    @Override
    public void release() {

    }
}
