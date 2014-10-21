package org.getalp.disambiguation.method.sequencial.entrydisambiguators;

import org.getalp.disambiguation.Document;
import org.getalp.disambiguation.configuration.Configuration;
import org.getalp.disambiguation.configuration.SubConfiguration;
import org.getalp.disambiguation.method.sequencial.parameters.WindowedLeskParameters;
import org.getalp.optimisation.functions.Function;
import org.getalp.optimisation.functions.setfunctions.input.SenseCombinationInput;
import org.getalp.optimisation.functions.setfunctions.submodular.Sum;
import org.getalp.similarity.local.SimilarityMeasure;

import java.util.ArrayList;
import java.util.List;


public class WindowedLeskLexicalEntryDisambiguator extends SequentialLexicalEntryDisambiguator {

    WindowedLeskParameters params;

    public WindowedLeskLexicalEntryDisambiguator(Configuration c, Document d, SimilarityMeasure sim,
                                                 WindowedLeskParameters params,
                                                 int start, int end, int currentIndex) {
        super(c, d, sim, start, end, currentIndex);
        this.params = params;
    }

    @Override
    public void run() {
        try {
            List<SubConfiguration> combinations = getCombinations(getStart(),
                    new SubConfiguration(getDocument(), getStart(), getEnd()),
                    getDocument());
            int selected = -1;
            if (getDocument().getSenses().get(getCurrentIndex()).size() == 1) {
                selected = 0;
            } else {
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

                Function f = new Sum(1);
                for (int cmb = 0; cmb < combinations.size(); cmb++) {
                    //String wordProgress = String.format("%.2f%%", (100d * ((double) cmb / (double) combinations.size())));
                    //System.err.print( progress + " ==> "+currentEntry.getLemma()+"#"+ currentEntry.getPos() + " ["+cmb+"/"+combinations.size()+" | "+wordProgress +"]\r");
                    SubConfiguration sc = combinations.get(cmb);
                    SenseCombinationInput sci = new SenseCombinationInput(sc, getDocument(),
                            getCurrentIndex() - sc.getStart(), getSimilarityMeasure());
                    double score = f.F(sci);
                    //System.err.println(score);
                    if (score <= minValue) {
                        prevMinValue = minValue;
                        minValue = score;
                        minIndex = cmb;
                    }
                    if (score >= maxValue) {
                        prevMaxValue = maxValue;
                        maxValue = score;
                        maxIndex = cmb;
                    }
                }
                double range = maxValue - minValue;
                double delta = range * params.getDeltaThreshold();

                if (((params.isMinimize() &&
                        (minIndex == -1 || Math.abs(prevMinValue - minValue) < params.getDeltaThreshold())) ||
                        (!params.isMinimize() &&
                                (maxIndex == -1 || Math.abs(prevMaxValue - maxValue) < params.getDeltaThreshold())))) {
                    selected = -1;
                } else if (params.isMinimize()) {
                    selected = combinations.get(minIndex).getAssignment(getCurrentIndex() - combinations.get(minIndex).getStart());
                } else {
                    selected = combinations.get(maxIndex).getAssignment(getCurrentIndex() - combinations.get(maxIndex).getStart());
                }


                if (params.isFallbackFS() && selected == -1) selected = 0;
            }
            getConfiguration().setSense(getCurrentIndex(), selected);
            getConfiguration().setConfidence(getCurrentIndex(), 1d);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public List<SubConfiguration> getCombinations(int currentIndex, SubConfiguration s, Document d) {
        List<SubConfiguration> combinations = new ArrayList<SubConfiguration>();
        if (currentIndex < s.size()) {
            for (int i = 0; i < d.getSenses(s, currentIndex).size(); i++) {
                SubConfiguration newSub = new SubConfiguration(s);
                newSub.setSense(currentIndex, i);
                combinations.addAll(getCombinations(currentIndex + 1, newSub, d));
            }

        } else {
            combinations.add(s);
        }
        return combinations;
    }
}
