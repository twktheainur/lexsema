package org.getalp.lexsema.wsd.method.sequencial.entrydisambiguators;

import org.getalp.lexsema.similarity.Document;
import org.getalp.lexsema.similarity.measures.SimilarityMeasure;
import org.getalp.lexsema.wsd.configuration.Configuration;
import org.getalp.lexsema.wsd.configuration.SubConfiguration;
import org.getalp.lexsema.wsd.method.sequencial.parameters.WindowedLeskParameters;
import org.getalp.lexsema.wsd.score.ConfigurationEntryPairwiseScoreInput;
import org.getalp.optimization.functions.Function;
import org.getalp.optimization.functions.input.FunctionInput;
import org.getalp.optimization.functions.setfunctions.submodular.Sum;

import java.util.ArrayList;
import java.util.List;


public class WindowedLeskLexicalEntryDisambiguator extends SequentialLexicalEntryDisambiguator {

    WindowedLeskParameters params;
    SimilarityMeasure similarityMeasure;

    public WindowedLeskLexicalEntryDisambiguator(Configuration c, Document d, SimilarityMeasure sim,
                                                 WindowedLeskParameters params,
                                                 int start, int end, int currentIndex) {
        super(c, d, start, end, currentIndex);
        this.params = params;
        similarityMeasure = sim;
    }

    @Override
    public void run() {
        try {
            SubConfiguration nsc = new SubConfiguration(getDocument(), getStart(), getEnd());
            List<SubConfiguration> combinations = getCombinations(0, nsc, getDocument());
            int selected = -1;
            if (getDocument().getSenses(getCurrentIndex()).size() == 1) {
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

                List<Double> results = new ArrayList<>();
                List<FunctionInput> inputs = new ArrayList<>();
                for (int cmb = 0; cmb < combinations.size(); cmb++) {
                    //String wordProgress = String.format("%.2f%%", (100d * ((double) cmb / (double) combinations.size())));
                    //System.err.print( progress + " ==> "+currentEntry.getLemma()+"#"+ currentEntry.getPos() + " ["+cmb+"/"+combinations.size()+" | "+wordProgress +"]\r");
                    Configuration sc = combinations.get(cmb);
                    FunctionInput sci = new ConfigurationEntryPairwiseScoreInput(sc, getDocument(),
                            getCurrentIndex() - sc.getStart(), similarityMeasure);
                    Function f = new Sum(1);
                    //LovaszExtention l = new LovaszExtention();
                    //f.setExtension(l);
                    //FunctionInput opt = l.optimize(new GradientOptimisation(), sci);
                    //sci.setInput(opt.getInput());
                    double score = f.F(sci);
                    //double score = l.compute(sci);
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
                    results.add(score);
                    inputs.add(sci);
                }
                double range = maxValue - minValue;

                if (params.isMinimize() && minIndex == -1 || !params.isMinimize() && maxIndex == -1) {
                    selected = -1;
                } else if (params.isMinimize()) {
                    selected = combinations.get(minIndex).getAssignment(getCurrentIndex() - combinations.get(minIndex).getStart());
                } else {
                    selected = combinations.get(maxIndex).getAssignment(getCurrentIndex() - combinations.get(maxIndex).getStart());
                }


                if (params.isFallbackFS() && selected == -1) {
                    selected = 0;
                }
            }
            getConfiguration().setSense(getCurrentIndex(), selected);
            getConfiguration().setConfidence(getCurrentIndex(), 1d);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public List<SubConfiguration> getCombinations(int currentIndex, SubConfiguration s, Document d) {
        List<SubConfiguration> combinations = new ArrayList<>();
        if (currentIndex < s.size()) {
            for (int i = 0; i < d.getSenses(s.getStart(), currentIndex).size(); i++) {
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
