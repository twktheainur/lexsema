package org.getalp.disambiguation.method.sequencial.entrydisambiguators;

import com.wcohen.ss.ScaledLevenstein;
import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.LexicalEntry;
import org.getalp.disambiguation.Sense;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.method.sequencial.parameters.SimplifiedLeskParameters;
import org.getalp.similarity.local.SimilarityMeasure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;


public class SimplifiedLeskLexicalEntryDisambiguator extends SequentialLexicalEntryDisambiguator {

    SimplifiedLeskParameters params;

    public SimplifiedLeskLexicalEntryDisambiguator(Configuration c, Document d, SimilarityMeasure sim, SimplifiedLeskParameters params, int start, int end, int currentIndex) {
        super(c, d, sim, start, end, currentIndex);
        this.params = params;
    }

    @Override
    public void run() {
        Collection<String> context;
        if (params.isOnlyUniqueWords()) {
            context = new TreeSet<String>();
        } else {
            context = new ArrayList<String>();
        }

        ScaledLevenstein sl = new ScaledLevenstein();
        for (int j = getStart(); j < getEnd(); j++) {
            if (params.isIncludeTarget() || (!params.isIncludeTarget() && j != getCurrentIndex())) {
                LexicalEntry cw = getDocument().getLexicalEntries().get(j);
                boolean isOverlap = false;
                if (params.isOnlyOverlapContexts()) {
                    for (Sense s : getDocument().getSenses().get(getCurrentIndex())) {
                        for (String wd : s.getSignature()) {
                            if (sl.score(cw.getSurfaceForm(), wd) > 0) {
                                isOverlap = true;
                                break;
                            }
                        }
                    }
                }
                if ((params.isOnlyOverlapContexts() && isOverlap) || (!params.isOnlyOverlapContexts())) {
                    for (String precWord : cw.getPrecedingNonInstances()) {
                        context.add(precWord);
                    }
                    context.add(cw.getSurfaceForm());
                }
                if (params.isAddSenseSignatures()) {
                    for (Sense s : getDocument().getSenses().get(j)) {
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

        for (int s = 0; s < getDocument().getSenses().get(getCurrentIndex()).size(); s++) {
            double score = getSimilarityMeasure().compute(getDocument().getSenses().get(getCurrentIndex()).get(s), lcontext);
            //System.err.println(score);
            if (score <= minValue) {
                prevMinValue = minValue;
                minValue = score;
                minIndex = s;
            }
            if (score >= maxValue) {
                prevMaxValue = maxValue;
                maxValue = score;
                maxIndex = s;
            }
        }
        double range = maxValue - minValue;
        double delta = range * params.getDeltaThreshold();
        if (!params.isAllowTies() && ((params.isMinimize() && Math.abs(prevMinValue - minValue) < params.getDeltaThreshold()) ||
                (!params.isMinimize() && Math.abs(prevMaxValue - maxValue) < params.getDeltaThreshold()))) {
            index = -1;
        } else if (params.isMinimize()) {
            index = minIndex;
        } else {
            index = maxIndex;
        }

        if (params.isFallbackFS() && index == -1) index = 0;
        getConfiguration().setSense(getCurrentIndex(), index);
        getConfiguration().setConfidence(getCurrentIndex(), 1d);
    }
}
